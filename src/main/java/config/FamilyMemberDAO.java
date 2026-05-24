package config;

import com.example.healthtrackcommunity.models.FamilyMember;
import com.google.firebase.database.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.regex.Pattern;

public class FamilyMemberDAO implements DAO<FamilyMember> {
    private DatabaseReference ref;
    private ObservableList<FamilyMember> familyMembers;

    public FamilyMemberDAO() {
        ref = FirebaseConnection.getDB().getReference("familyMembers");
        familyMembers = FXCollections.observableArrayList();
        subscribe();
    }

    private void subscribe() {
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String prev) {
                FamilyMember fm = snapshot.getValue(FamilyMember.class);
                if (fm != null) familyMembers.add(fm);
            }
            @Override public void onChildChanged(DataSnapshot snapshot, String prev) {
                FamilyMember fm = snapshot.getValue(FamilyMember.class);
                if (fm != null) { familyMembers.remove(fm); familyMembers.add(fm); }
            }
            @Override public void onChildRemoved(DataSnapshot snapshot) {
                FamilyMember fm = snapshot.getValue(FamilyMember.class);
                if (fm != null) familyMembers.remove(fm);
            }
            @Override public void onChildMoved(DataSnapshot snapshot, String prev) {}
            @Override public void onCancelled(DatabaseError error) {}
        });
    }

    @Override
    public ObservableList<FamilyMember> getAll() { return familyMembers; }

    @Override
    public FamilyMember get(String id) {
        for (FamilyMember fm : familyMembers)
            if (fm.getId().equals(id)) return fm;
        return null;
    }

    @Override
    public void create(FamilyMember fm) {
        if (!validateEmail(fm.getEmail()) || !validatePassword(fm.getPassword())) return;
        if (emailExists(fm.getEmail())) return;
        DatabaseReference pushed = ref.push();
        fm.setId(pushed.getKey());
        pushed.setValueAsync(fm);
    }

    public void create(FamilyMember fm, Runnable success, Runnable fail, Runnable emailUsed) {
        if (!validateEmail(fm.getEmail()) || !validatePassword(fm.getPassword())) {
            fail.run(); return;
        }
        if (emailExists(fm.getEmail())) {
            emailUsed.run(); return;
        }
        DatabaseReference pushed = ref.push();
        fm.setId(pushed.getKey());
        pushed.setValue(fm, (error, ref) -> {
            if (error == null) success.run(); else fail.run();
        });
    }

    @Override
    public void update(FamilyMember fm) {
        if (fm.getId() != null) ref.child(fm.getId()).setValueAsync(fm);
    }

    @Override
    public void delete(FamilyMember fm) {
        if (fm.getId() != null) ref.child(fm.getId()).removeValueAsync();
    }

    private boolean emailExists(String email) {
        for (FamilyMember fm : familyMembers)
            if (fm.getEmail().equals(email)) return true;
        return false;
    }

    private boolean validateEmail(String email) {
        String regex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email != null && Pattern.compile(regex).matcher(email).matches();
    }

    private boolean validatePassword(String pwd) {
        return pwd != null && pwd.length() >= 6 && pwd.length() <= 24 &&
                pwd.matches(".*[a-zA-Z].*") && pwd.matches(".*[0-9].*");
    }
}
