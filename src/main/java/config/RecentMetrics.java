package config;

import com.example.healthtrackcommunity.models.*;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;

public class RecentMetrics {

    private final ObservableList<Metric> recent;
    private final Patient patient;

    public RecentMetrics(Patient patient) {
        this.patient = patient;
        recent = FXCollections.observableArrayList();
        getRecentMetrics();
    }

    public ObservableList<Metric> getRecent() {
        return recent;
    }

    private void getRecentMetrics() {
        DatabaseReference ref = FirebaseConnection.getDB().getReference("metrics");

        metricQuery(ref.child("pressure"), PressureMetric.class);
        metricQuery(ref.child("heartRate"), HeartRateMetric.class);
        metricQuery(ref.child("glucose"), GlucoseMetric.class);
        metricQuery(ref.child("weight"), WeightMetric.class);
    }

    private void metricQuery(DatabaseReference ref, Class <? extends Metric> metricClass) {
        ref.orderByChild("userId").equalTo(patient.getId()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Metric m = dataSnapshot.getValue(metricClass);
                if (isInLastWeek(m.getDateObj())) {
                    recent.add(m);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Metric m = dataSnapshot.getValue(metricClass);
                if (recent.contains(m)) {
                    recent.remove(m);
                    recent.add(m);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Metric m = dataSnapshot.getValue(metricClass);
                recent.remove(m);
            }

            @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override public void onCancelled(DatabaseError databaseError) {}
        });
    }


    private boolean isInLastWeek(LocalDate date) {
        LocalDate lastWeek = LocalDate.now().minusWeeks(1);
        return date.isAfter(lastWeek);
    }

}
