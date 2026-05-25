package config;

import com.example.healthtrackcommunity.models.Comment;
import com.example.healthtrackcommunity.models.Doctor;
import com.example.healthtrackcommunity.models.Patient;
import com.google.firebase.database.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class CommentDAO implements DAO<Comment> {

    private final DatabaseReference ref;
    private final ObservableList<Comment> comments;

    private final Doctor doctor;
    private final Patient patient;

    public CommentDAO() {
        ref = FirebaseConnection.getDB().getReference("comments");
        comments = FXCollections.observableArrayList();

        doctor = null;
        patient = null;

        subscribe();
    }

    //solo comentarios de un doctor
    public CommentDAO(Doctor doctor) {
        ref = FirebaseConnection.getDB().getReference("comments");
        comments = FXCollections.observableArrayList();

        this.doctor = doctor;
        patient = null;

        subscribe();
    }

    //solo comentarios de un paciente
    public CommentDAO(Patient patient) {
        ref = FirebaseConnection.getDB().getReference("comments");
        comments = FXCollections.observableArrayList();

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
            /*remove usa equals para encontrar el objeto. Como equals compara únicamente por id, podemos remover y agregar el actualizado.*/

            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
                Comment c = snapshot.getValue(Comment.class);
                comments.add(c);
            }

            @Override
            public void onChildChanged(DataSnapshot snapshot, String previousChildName) {
                Comment c = snapshot.getValue(Comment.class);
                comments.remove(c);
                comments.add(c);
            }

            @Override
            public void onChildRemoved(DataSnapshot snapshot) {
                Comment c = snapshot.getValue(Comment.class);
                comments.remove(c);
            }

            @Override public void onChildMoved(DataSnapshot snapshot, String previousChildName) {}
            @Override public void onCancelled(DatabaseError error) {}
        });
    }

    @Override
    public ObservableList<Comment> getAll() {
        return comments;
    }

    @Override
    public Comment get(String id) {
        for (Comment c : comments) {
            if (c.getId().equals(id)) return c;
        }
        return null;
    }

    @Override
    public void create(Comment c) {
        DatabaseReference pushed = ref.push();
        c.setId(pushed.getKey());
        pushed.setValueAsync(c);

        System.out.println("Comentario creado para paciente: " + c.getPatientId());
    }

    @Override
    public void update(Comment c) {
        ref.child(c.getId()).setValueAsync(c);
    }

    @Override
    public void delete(Comment c) {
        ref.child(c.getId()).removeValueAsync();
    }

    /* FUNCIONES CON CALLBACKS */

    public void create(Comment c, Runnable success, Runnable fail) {
        DatabaseReference pushed = ref.push();
        c.setId(pushed.getKey());

        pushed.setValue(c, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError error, DatabaseReference databaseReference) {
                if (error == null) {
                    System.out.println("Comentario creado.");
                    success.run();
                } else {
                    System.out.println("No se pudo crear el comentario.");
                    fail.run();
                }
            }
        });
    }
}