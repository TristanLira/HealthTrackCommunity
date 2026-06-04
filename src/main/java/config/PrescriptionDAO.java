package config;

import com.example.healthtrackcommunity.models.Patient;
import com.example.healthtrackcommunity.models.Prescription;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.checkerframework.checker.units.qual.C;

public class PrescriptionDAO implements DAO<Prescription> {

    private DatabaseReference ref;
    private ObservableList<Prescription> prescriptions;
    private Patient patient;

    public PrescriptionDAO(Patient patient) {
        this.patient = patient;
        prescriptions = FXCollections.observableArrayList();
        ref = FirebaseConnection.getDB().getReference("prescriptions");

        subscribe();
    }

    private void subscribe() {
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String s) {
                Prescription p = snapshot.getValue(Prescription.class);
                prescriptions.add(p);
            }

            @Override
            public void onChildChanged(DataSnapshot snapshot, String s) {
                Prescription p = snapshot.getValue(Prescription.class);
                prescriptions.remove(p);
                prescriptions.add(p);
            }

            @Override
            public void onChildRemoved(DataSnapshot snapshot) {
                Prescription p = snapshot.getValue(Prescription.class);
                prescriptions.remove(p);
            }

            @Override public void onChildMoved(DataSnapshot snapshot, String s) {}
            @Override public void onCancelled(DatabaseError databaseError) {}
        });
    }

    @Override
    public ObservableList<Prescription> getAll() {
        return prescriptions;
    }

    @Override
    public Prescription get(String id) {
        for (Prescription i: prescriptions) {
            if (i.getId().equals(id)) return i;
        }
        return null;
    }

    @Override
    public void create(Prescription p) {
        DatabaseReference pushed = ref.push();
        p.setId(pushed.getKey());
        pushed.setValueAsync(p);
    }

    @Override
    public void update(Prescription p) {
        ref.child(p.getId()).setValueAsync(p);
    }

    @Override
    public void delete(Prescription p) {
        ref.child(p.getId()).removeValueAsync();
    }

    /*callbacks*/

    public void create(Prescription p, Runnable success, Runnable fail) {
        DatabaseReference pushed = ref.push();
        p.setId(pushed.getKey());

        pushed.setValue(p, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError error, DatabaseReference databaseReference) {
                if (error == null) {
                    success.run();
                } else {
                    fail.run();
                }
            }
        });
    }
}
