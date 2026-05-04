package config;

import com.example.healthtrackcommunity.models.Doctor;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.print.Doc;


public class DoctorDAO implements DAO <Doctor> {

    private DatabaseReference ref;

    private ObservableList<Doctor> doctors;

    public DoctorDAO() {
        ref = FirebaseConnection.getDB().getReference("doctors");
        subscribe();
    }

    private void subscribe() {
        doctors = FXCollections.observableArrayList();

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
                Doctor d = snapshot.getValue(Doctor.class);
                doctors.add(d);
            }

            @Override
            public void onChildChanged(DataSnapshot snapshot, String previousChildName) {
                Doctor d = snapshot.getValue(Doctor.class);

                /*remove usa equals para encontrar que elemento eliminar. Ya que en equals de doctor se hace la comparación
                * únicamente por el id (email), se puede eliminar el objeto anterior y agregar el actualizado solo con remove.*/
                doctors.remove(d);
                doctors.add(d);
            }

            @Override
            public void onChildRemoved(DataSnapshot snapshot) {
                Doctor d = snapshot.getValue(Doctor.class);
                doctors.remove(d);
            }

            @Override public void onChildMoved(DataSnapshot snapshot, String previousChildName) {}
            @Override public void onCancelled(DatabaseError error) {}
        });
    }

    @Override
    public ObservableList<Doctor> getAll() {
        return doctors;
    }

    @Override
    public Doctor get(String id) {
        for (Doctor d: doctors) {
            if (d.getEmail().equals(id)) return d;
        }
        return null;
    }

    @Override
    public void create(Doctor d) {
        if (validateEmail(d.getEmail()) && validatePassword(d.getPassword())) {
            ref.child(d.getEmail()).setValueAsync(d);
        }
    }

    @Override
    public void update(Doctor d) {
        if (!doctors.contains(d)) return;
        ref.child(d.getEmail()).setValueAsync(d);
    }

    @Override
    public void delete(Doctor d) {
        ref.child(d.getEmail()).removeValueAsync();
    }


    //TODO implementar validaciones con regex

    private boolean validatePassword(String password) {
        return true;
    }

    private boolean validateEmail(String email) {
        return true;
    }
}
