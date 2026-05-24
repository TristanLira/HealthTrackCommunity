package config;

import com.example.healthtrackcommunity.models.Admin;
import com.google.firebase.database.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class AdminDAO implements DAO<Admin> {
    private DatabaseReference ref;
    private ObservableList<Admin> admins;

    public AdminDAO() {
        ref = FirebaseConnection.getDB().getReference("admins");
        admins = FXCollections.observableArrayList();
        subscribe();
    }

    private void subscribe() {
        ref.addChildEventListener(new ChildEventListener() {
            @Override public void onChildAdded(DataSnapshot snapshot, String prev) {
                Admin a = snapshot.getValue(Admin.class);
                if (a != null) admins.add(a);
            }
            @Override public void onChildChanged(DataSnapshot snapshot, String prev) {
                Admin a = snapshot.getValue(Admin.class);
                if (a != null) { admins.remove(a); admins.add(a); }
            }
            @Override public void onChildRemoved(DataSnapshot snapshot) {
                Admin a = snapshot.getValue(Admin.class);
                if (a != null) admins.remove(a);
            }
            @Override public void onChildMoved(DataSnapshot snapshot, String prev) {}
            @Override public void onCancelled(DatabaseError error) {}
        });
    }

    @Override
    public ObservableList<Admin> getAll() { return admins; }

    @Override
    public Admin get(String id) {
        for (Admin a : admins) if (a.getId().equals(id)) return a;
        return null;
    }

    @Override
    public void create(Admin a) {
        if (a.getId() == null) {
            DatabaseReference pushed = ref.push();
            a.setId(pushed.getKey());
            pushed.setValueAsync(a);
        }
    }

    @Override
    public void update(Admin a) {
        if (a.getId() != null) ref.child(a.getId()).setValueAsync(a);
    }

    @Override
    public void delete(Admin a) {
        if (a.getId() != null) ref.child(a.getId()).removeValueAsync();
    }
}
