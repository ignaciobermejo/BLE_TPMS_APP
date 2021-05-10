package com.bletpms.app;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.bletpms.app.database.AppDatabase;
import com.bletpms.app.database.Vehicle;
import com.bletpms.app.database.VehicleDAO;
import com.bletpms.app.testCommon.TestUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static com.bletpms.app.testCommon.LiveDataTestUtil.getOrAwaitValue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(AndroidJUnit4.class)
public class DatabaseTest {
    private VehicleDAO vehicleDAO;
    private AppDatabase db;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        vehicleDAO = db.vehicleDAO();
    }

    @After
    public void closeDb() {
        db.close();
    }

    @Test
    public void insertAndReadList() throws Exception {
        Vehicle vehicle = TestUtil.createVehicle("testVehicle");
        vehicleDAO.insert(vehicle);
        List<Vehicle> list = getOrAwaitValue(vehicleDAO.getAll());
        assertThat(list.size(), is(1));
    }

    @Test
    public void insertDeleteAndReadList() throws Exception {
        Vehicle vehicle = TestUtil.createVehicle("testVehicle");
        vehicleDAO.insert(vehicle);
        List<Vehicle> list = getOrAwaitValue(vehicleDAO.getAll());
        assertThat(list.size(), is(1));
        vehicleDAO.delete(list.get(0));
        list = getOrAwaitValue(vehicleDAO.getAll());
        assertThat(list.size(), is(0));
    }

    @Test
    public void insertUpdateAndReadList() throws Exception {
        Vehicle vehicle = TestUtil.createVehicle("testVehicle");
        vehicleDAO.insert(vehicle);
        List<Vehicle> list = getOrAwaitValue(vehicleDAO.getAll());
        assertThat(list.size(), is(1));
        assertThat(list.get(0).getType(), is("4"));
        Vehicle updatedVehicle = list.get(0);
        updatedVehicle.setType("2");
        vehicleDAO.update(updatedVehicle);
        assertThat(list.get(0).getType(), is("2"));
    }

    @Test
    public void insertUpdateAllAndReadList() throws Exception {
        Vehicle vehicle = TestUtil.createVehicle("testVehicle");
        vehicleDAO.insert(vehicle);
        Vehicle vehicle2 = TestUtil.createVehicle("testVehicle2");
        vehicleDAO.insert(vehicle2);
        List<Vehicle> list = getOrAwaitValue(vehicleDAO.getAll());
        assertThat(list.size(), is(2));
        assertThat(list.get(0).getType(), is("4"));
        assertThat(list.get(1).getType(), is("4"));
        for (Vehicle v:list) {
            v.setType("2");
        }
        vehicleDAO.updateAll(list);
        assertThat(list.get(0).getType(), is("2"));
        assertThat(list.get(1).getType(), is("2"));
    }

    @Test
    public void insertAndGetMainVehicle() throws Exception {
        Vehicle vehicle = TestUtil.createVehicle("testVehicle");
        vehicleDAO.insert(vehicle);
        Vehicle vehicle2 = TestUtil.createVehicle("testVehicle2");
        vehicle2.setMain(false);
        vehicleDAO.insert(vehicle2);
        List<Vehicle> list = getOrAwaitValue(vehicleDAO.getAll());
        assertThat(list.size(), is(2));
        Vehicle mainVehicle = getOrAwaitValue(vehicleDAO.getMain());
        assertThat(mainVehicle.getName(), is("testVehicle"));
        assertThat(mainVehicle.getMain(), is(true));
    }
}
