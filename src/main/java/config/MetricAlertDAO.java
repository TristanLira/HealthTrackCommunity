package config;


import com.example.healthtrackcommunity.models.Doctor;
import com.example.healthtrackcommunity.models.MetricAlert;
import com.example.healthtrackcommunity.models.Patient;
import com.google.firebase.database.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class MetricAlertDAO
        implements DAO<MetricAlert> {

    private final DatabaseReference ref;
    private final ObservableList<MetricAlert> alerts;

    private final Doctor doctor;
    private final Patient patient;

    public MetricAlertDAO() {
        ref = FirebaseConnection.getDB().getReference("metricAlerts");
        alerts = FXCollections.observableArrayList();

        doctor = null;
        patient = null;

        subscribe();
    }

    //solo alertas de un doctor
    public MetricAlertDAO(Doctor doctor) {
        ref = FirebaseConnection.getDB().getReference("metricAlerts");
        alerts = FXCollections.observableArrayList();

        this.doctor = doctor;
        patient = null;

        subscribe();
    }

    //solo alertas de un paciente
    public MetricAlertDAO(Patient patient) {
        ref = FirebaseConnection.getDB().getReference("metricAlerts");
        alerts = FXCollections.observableArrayList();

        this.patient = patient;
        doctor = null;

        subscribe();
    }

    private void subscribe() {
        Query query;

        if (doctor != null) query = ref.orderByChild("doctorId").equalTo(doctor.getId());

        else if (patient != null) query = ref.orderByChild("patientId").equalTo(patient.getId());

        else query = ref;

        query.addChildEventListener(new ChildEventListener() {
            /*remove usa equals para encontrar el objeto. Como equals compara únicamente por id,podemos remover y agregar el actualizado.*/

            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
                MetricAlert a = snapshot.getValue(MetricAlert.class);
                alerts.add(a);
            }

            @Override
            public void onChildChanged(DataSnapshot snapshot, String previousChildName) {
                MetricAlert a = snapshot.getValue(MetricAlert.class);
                alerts.remove(a);
                alerts.add(a);
            }

            @Override
            public void onChildRemoved(DataSnapshot snapshot) {
                MetricAlert a = snapshot.getValue(MetricAlert.class);
                alerts.remove(a);
            }

            @Override public void onChildMoved(DataSnapshot snapshot, String previousChildName) {}
            @Override public void onCancelled(DatabaseError error) {}
        });
    }

    @Override
    public ObservableList<MetricAlert> getAll() {
        return alerts;
    }

    @Override
    public MetricAlert get(String id) {
        for (MetricAlert a : alerts) {
            if (a.getId().equals(id)) return a;
        }
        return null;
    }

    @Override
    public void create(MetricAlert a) {
        DatabaseReference pushed = ref.push();

        a.setId(pushed.getKey());
        pushed.setValueAsync(a);

        System.out.println("Alerta creada para paciente: " + a.getPatientId());
    }

    @Override
    public void update(MetricAlert a) {
        // if (!alerts.contains(a)) return;
        ref.child(a.getId()).setValueAsync(a);
    }

    @Override
    public void delete(MetricAlert a) {
        // if (!alerts.contains(a)) return;
        ref.child(a.getId()).removeValueAsync();
    }

    /* FUNCIONES CON CALLBACKS */

    public void create(MetricAlert a, Runnable success, Runnable fail) {
        DatabaseReference pushed = ref.push();

        a.setId(pushed.getKey());

        pushed.setValue(a, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError error, DatabaseReference databaseReferenc) {
                if (error == null) {
                    System.out.println("Alerta creada.");
                    success.run();
                } else {
                    System.out.println("No se pudo crear la alerta.");
                    fail.run();
                }
            }
        });
    }
}