package config;

import com.example.healthtrackcommunity.models.*;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetricDAO implements DAO<Metric> {

    public static final int HEART_RATE = 0;
    public static final int PRESSURE = 1;
    public static final int GLUCOSE = 2;
    public static final int WEIGHT = 3;
    private static final Logger log = LoggerFactory.getLogger(MetricDAO.class);

    private DatabaseReference ref;
    private ObservableList<Metric> metrics;
    private Class<? extends Metric> childClass;
    private Patient logged;

    public MetricDAO(Patient logged, int type) {
        metrics = FXCollections.observableArrayList();
        this.logged = logged;

        switch (type) {
            case MetricDAO.HEART_RATE:
                ref = FirebaseConnection.getDB().getReference("metrics/heartRate");
                childClass = HeartRateMetric.class;
                break;

            case MetricDAO.PRESSURE:
                ref = FirebaseConnection.getDB().getReference("metrics/pressure");
                childClass = PressureMetric.class;
                break;

            case MetricDAO.GLUCOSE:
                ref = FirebaseConnection.getDB().getReference("metrics/glucose");
                childClass = GlucoseMetric.class;
                break;

            case MetricDAO.WEIGHT:
                ref = FirebaseConnection.getDB().getReference("metrics/weight");
                childClass = WeightMetric.class;
                break;
        }

        subscribe();
    }

    private void subscribe() {
        ref.orderByChild("userId").equalTo(logged.getId()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
                Metric m = snapshot.getValue(childClass);
                metrics.add(m);
            }

            @Override
            public void onChildChanged(DataSnapshot snapshot, String previousChildName) {
                Metric m = snapshot.getValue(childClass);
                metrics.remove(m);
                metrics.add(m);
            }

            @Override
            public void onChildRemoved(DataSnapshot snapshot) {
                Metric m = snapshot.getValue(childClass);
                metrics.remove(m);
            }

            @Override public void onChildMoved(DataSnapshot snapshot, String previousChildName) {}
            @Override public void onCancelled(DatabaseError error) {}
        });
    }

    @Override
    public ObservableList<Metric> getAll() {
        return metrics;
    }

    @Override
    public Metric get(String id) {
        for (Metric i: metrics) {
            if (i.getId().equals(id)) return i;
        }
        return null;
    }

    @Override
    public void create(Metric m) {
        if (!childClass.isInstance(m)) {
            System.out.println("La medición no se pudo registrar ya que no es del tipo correcto.");
            return;
        }

        DatabaseReference pushed = ref.push();
        m.setId(pushed.getKey());
        pushed.setValueAsync(m);
    }

    @Override
    public void update(Metric m) {
        if (!metrics.contains(m)) return;
        ref.child(m.getId()).setValueAsync(m);
    }

    @Override
    public void delete(Metric m) {
        if (!metrics.contains(m)) return;
        ref.child(m.getId()).removeValueAsync();
    }
}
