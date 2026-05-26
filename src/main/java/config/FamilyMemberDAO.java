package config;

import com.example.healthtrackcommunity.models.FamilyMember;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
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
            public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
                FamilyMember f = snapshot.getValue(FamilyMember.class);
                familyMembers.add(f);
            }

            @Override
            public void onChildChanged(DataSnapshot snapshot, String previousChildName) {
                FamilyMember f = snapshot.getValue(FamilyMember.class);

                /* remove usa equals para encontrar el objeto.
                 * En equals de FamilyMember se compara solo el id,
                 * por lo que basta con remove y add.
                 */
                familyMembers.remove(f);
                familyMembers.add(f);
            }

            @Override
            public void onChildRemoved(DataSnapshot snapshot) {
                FamilyMember f = snapshot.getValue(FamilyMember.class);
                familyMembers.remove(f);
            }

            @Override public void onChildMoved(DataSnapshot snapshot, String previousChildName) {}
            @Override public void onCancelled(DatabaseError error) {}
        });
    }

    @Override
    public ObservableList<FamilyMember> getAll() {
        return familyMembers;
    }

    @Override
    public FamilyMember get(String id) {
        for (FamilyMember f: familyMembers) {
            if (f.getId().equals(id)) return f;
        }
        return null;
    }

    @Override
    public void create(FamilyMember f) {
        if (!(validateEmail(f.getEmail()) && validatePassword(f.getPassword()))) {
            System.out.println("Email o contraseña incorrectos. No se creó la cuenta del familiar: " + f.getEmail());
            return;
        }

        if (emailRegistered(f)) {
            System.out.println("Familiar existente, no se pudo crear la cuenta.");
            return;
        }

        DatabaseReference pushed = ref.push();
        f.setId(pushed.getKey());

        pushed.setValueAsync(f);

        System.out.println("Cuenta de familiar creada: " + f.getEmail());
    }

    private boolean emailRegistered(FamilyMember f) {
        for (FamilyMember i : familyMembers) {
            if (i.getEmail().equals(f.getEmail())) return true;
        }
        return false;
    }

    @Override
    public void update(FamilyMember f) {
        if (!familyMembers.contains(f)) return;

        ref.child(f.getId()).setValueAsync(f);
    }

    @Override
    public void delete(FamilyMember f) {
        if (!familyMembers.contains(f)) return;
        ref.child(f.getId()).removeValueAsync();
    }

    /* FUNCIONES CON CALLBACKS */

    public void create(FamilyMember f, Runnable success, Runnable fail, Runnable emailUsed) {
        if (!(validateEmail(f.getEmail()) && validatePassword(f.getPassword()))) {
            System.out.println("Email o contraseña incorrectos. No se creó la cuenta del familiar: " + f.getEmail());
            fail.run();
            return;
        }

        if (emailRegistered(f)) {
            System.out.println("Familiar existente, no se pudo crear la cuenta.");
            emailUsed.run();
            return;
        }

        DatabaseReference pushed = ref.push();
        f.setId(pushed.getKey());

        pushed.setValue(f, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError error, DatabaseReference databaseReference) {
                if (error == null) {
                    System.out.println("Cuenta de familiar creada: " + f.getEmail());
                    success.run();
                } else {
                    System.out.println("No se pudo crear la cuenta del familiar: "
                            + f.getEmail());
                    fail.run();
                }
            }
        });
    }

    public void update(FamilyMember f, Runnable success, Runnable fail) {
        if (!familyMembers.contains(f)) {
            fail.run();
            return;
        }

        ref.child(f.getId()).setValue(f, new DatabaseReference.CompletionListener() {
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

    /* VALIDACIONES */

    private boolean validateEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
                        "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

        Pattern p = Pattern.compile(emailRegex);

        return email != null && p.matcher(email).matches();
    }

    private boolean validatePassword(String password) {
        String letters = "[a-zA-Z]+";
        String numbers = "[0-9]+";

        Pattern p1 = Pattern.compile(letters);
        Pattern p2 = Pattern.compile(numbers);

        return password != null &&
                p1.matcher(password).find() &&
                p2.matcher(password).find() &&
                password.length() >= 6 &&
                password.length() <= 24;
    }
}
