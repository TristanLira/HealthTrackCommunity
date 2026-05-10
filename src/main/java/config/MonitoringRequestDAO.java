package config;

import com.example.healthtrackcommunity.models.Doctor;
import com.example.healthtrackcommunity.models.MonitoringRequest;
import com.example.healthtrackcommunity.models.Patient;
import com.google.firebase.database.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class MonitoringRequestDAO implements DAO<MonitoringRequest> {

    static final int DOCTOR_TYPE = 0, PATIENT_TYPE = 1;

    private DatabaseReference ref;

    private final int type;
    private Patient p;
    private Doctor d;

    ObservableList<MonitoringRequest> requests;

    public MonitoringRequestDAO(Patient p) {
        ref = FirebaseConnection.getDB().getReference("monitoringRequests");
        requests = FXCollections.observableArrayList();

        type = PATIENT_TYPE;
        this.p = p;
        subscribe();
    }

    public MonitoringRequestDAO(Doctor d) {
        ref = FirebaseConnection.getDB().getReference("monitoringRequests");
        requests = FXCollections.observableArrayList();

        type = DOCTOR_TYPE;
        this.d = d;
        subscribe();
    }

    private void subscribe() {
        Query query;

        /*si es de tipo paciente solo guarda las solicitudes del paciente,
        * si es de tipo médico guarda todas las solicitudes que este ha recibido*/
        switch (type) {
            case DOCTOR_TYPE:
                query = ref.orderByChild("doctorId").equalTo(d.getId());
                break;

            case PATIENT_TYPE:
                query = ref.orderByChild("patientId").equalTo(p.getId());
                break;

            default:
                query = ref;
                break;
        }

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String s) {
                MonitoringRequest m = snapshot.getValue(MonitoringRequest.class);
                requests.add(m);
            }

            @Override
            public void onChildChanged(DataSnapshot snapshot, String s) {
                MonitoringRequest m = snapshot.getValue(MonitoringRequest.class);
                requests.remove(m);
                requests.add(m);
            }

            @Override
            public void onChildRemoved(DataSnapshot snapshot) {
                MonitoringRequest m = snapshot.getValue(MonitoringRequest.class);
                requests.remove(m);
            }

            @Override public void onChildMoved(DataSnapshot snapshot, String s) {}
            @Override public void onCancelled(DatabaseError databaseError) {}
        });
    }


    @Override
    public ObservableList<MonitoringRequest> getAll() {
        return requests;
    }

    @Override
    public MonitoringRequest get(String id) {
        for (MonitoringRequest i: requests) {
            if (i.getId().equals(id)) return i;
        }
        return null;
    }

    @Override
    public void create(MonitoringRequest m) {
        if (type == DOCTOR_TYPE) return; //un doctor no puede crear solicitudes

        //elimina todas las solicitudes antes de mandar la nueva
        if (!requests.isEmpty()) {
            for (MonitoringRequest i: requests) delete(i);
        }

        DatabaseReference pushed = ref.push();
        m.setId(pushed.getKey());
        pushed.setValueAsync(m);
    }

    //no se pueden actualizar las solicitudes
    @Override public void update(MonitoringRequest m) {}

    @Override
    public void delete(MonitoringRequest m) {
        if (!requests.contains(m)) return;
        ref.child(m.getId()).removeValueAsync();
    }


    /*METODOS CON CALLBACKS*/

    public void create(MonitoringRequest m, Runnable success, Runnable fail) {
        if (type == DOCTOR_TYPE) return; //un doctor no puede crear solicitudes

        //elimina todas las solicitudes antes de mandar la nueva
        if (!requests.isEmpty()) {
            for (MonitoringRequest i: requests) delete(i);
        }

        DatabaseReference pushed = ref.push();
        m.setId(pushed.getKey());

        pushed.setValue(m, new DatabaseReference.CompletionListener() {
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
