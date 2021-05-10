package com.bletpms.app.database;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class VehicleRepository {
    private final VehicleDAO mVehicleDAO;
    private final LiveData<List<Vehicle>> mAllVehicles;
    private final LiveData<Vehicle> mMainVehicle;

    public VehicleRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        mVehicleDAO = db.vehicleDAO();
        mAllVehicles = mVehicleDAO.getAll();
        mMainVehicle = mVehicleDAO.getMain();
    }

    public LiveData<List<Vehicle>> getAllVehicles() {
        return mAllVehicles;
    }

    public LiveData<Vehicle> getMainVehicle() { return mMainVehicle; }

    public void insert(Vehicle vehicle){
        //new insertAsyncTask(mVehicleDAO).execute(vehicle);
        AppDatabase.databaseWriterExecutor.execute(() -> {
            List<Vehicle>mains = mVehicleDAO.getMains();
            for (Vehicle v:mains) {
                v.setMain(false);
            }
            mVehicleDAO.updateAll(mains);
            mVehicleDAO.insert(vehicle);
        });
    }

    public void delete(Vehicle vehicle){
        //new deleteAsyncTask(mVehicleDAO).execute(vehicle);
        AppDatabase.databaseWriterExecutor.execute(() -> mVehicleDAO.delete(vehicle));
    }

    public void update(Vehicle vehicle){
        //new updateAsyncTask(mVehicleDAO).execute(vehicle);
        AppDatabase.databaseWriterExecutor.execute(() -> mVehicleDAO.update(vehicle));
    }

    public void setMain(Vehicle newMainVehicle){
        Vehicle old = mMainVehicle.getValue();
        if (old != null) old.setMain(false);
        //new updateAsyncTask(mVehicleDAO).execute(old);
        update(old);
        newMainVehicle.setMain(true);
        update(newMainVehicle);
        //new updateAsyncTask(mVehicleDAO).execute(newMainVehicle);
    }
    /*
    public Vehicle getMain(){
        final Vehicle[] vehicle = new Vehicle[1];
        AsyncTask.execute(() -> vehicle[0] = mVehicleDAO.getMainSync());
        return vehicle[0];
    }

    public String[] getDevicesSync(){
        return new getDevicesAsyncTask(mVehicleDAO).get();
    }

    private static class insertAsyncTask extends AsyncTask<Vehicle, Void, Void> {

        private final VehicleDAO mAsyncTaskDao;

        insertAsyncTask(VehicleDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Vehicle... params) {
            List<Vehicle>mains = mAsyncTaskDao.getMains();
            for (Vehicle v:mains) {
                v.setMain(false);
            }
            mAsyncTaskDao.updateAll(mains);
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }

    private static class deleteAsyncTask extends AsyncTask<Vehicle, Void, Void>{

        private final VehicleDAO mAsyncTaskDao;

        private deleteAsyncTask(VehicleDAO mAsyncTaskDao) {
            this.mAsyncTaskDao = mAsyncTaskDao;
        }

        @Override
        protected Void doInBackground(final Vehicle... params) {
            mAsyncTaskDao.delete(params[0]);
            return null;
        }
    }

    private static class updateAsyncTask extends AsyncTask<Vehicle, Void, Void>{

        private final VehicleDAO mAsyncTaskDao;

        private updateAsyncTask(VehicleDAO mAsyncTaskDao) {
            this.mAsyncTaskDao = mAsyncTaskDao;
        }

        @Override
        protected Void doInBackground(final Vehicle... params) {
            mAsyncTaskDao.update(params[0]);
            return null;
        }
    }

    private static class getDevicesAsyncTask extends AsyncTask<Void,Void,String[]>{

        private final VehicleDAO mAsyncTaskDao;

        private getDevicesAsyncTask(VehicleDAO mAsyncTaskDao) {
            this.mAsyncTaskDao = mAsyncTaskDao;
        }

        @Override
        protected String[] doInBackground(Void... voids) {
            return this.mAsyncTaskDao.getDevicesSync();
        }
    }*/
}
