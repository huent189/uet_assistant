package vnu.uet.mobilecourse.assistant.database.DAO;

import android.util.Log;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import vnu.uet.mobilecourse.assistant.exception.DocumentNotFoundException;
import vnu.uet.mobilecourse.assistant.model.event.EventComparator;
import vnu.uet.mobilecourse.assistant.model.firebase.Connection;
import vnu.uet.mobilecourse.assistant.model.firebase.Todo;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateMediatorLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateStatus;

public class ConnectionDAO extends FirebaseDAO<Connection> {

    public ConnectionDAO() {
        super(FirebaseFirestore.getInstance().collection(FirebaseCollectionName.CONNECTION));
    }

    @Override
    protected void handleDocumentNotFound(StateMediatorLiveData<Connection> response, String id) {
        mColReference.document(id)
                .addSnapshotListener((snapshot, e) -> {
                    // catch an exception
                    if (e != null) {
                        Log.e(TAG, "Listen to data list failed.");
                        response.postError(e);
                    }
                    // hasn't got snapshots yet
                    else if (snapshot == null) {
                        Log.d(TAG, "Listening to data list.");
                        response.postLoading();
                    }
                    // query completed with snapshots
                    else {
                        Connection connection = snapshot.toObject(Connection.class);

                        if (connection == null) {
                            response.postError(new DocumentNotFoundException(id));
                        } else {
                            response.postSuccess(connection);
                        }
                    }
                });
    }

    @Override
    public StateLiveData<List<Connection>> readAll() {
        // this live data will only initialize once
        // data change will auto update by 'addSnapshotListener'
        // to listen for data changes
        if (mDataList == null) {
            // initialize with loading state
            mDataList = new StateLiveData<>(new StateModel<>(StateStatus.LOADING));

            // listen data from firebase
            // query all document owned by current user
            mColReference.whereArrayContains("studentIds", STUDENT_ID)
                    // order
                    // listen for data change
                    .addSnapshotListener((snapshots, e) -> {
                            // catch an exception
                            if (e != null) {
                                Log.e(TAG, "Listen to data list failed.");
                                mDataList.postError(e);
                            }
                            // hasn't got snapshots yet
                            else if (snapshots == null) {
                                Log.d(TAG, "Listening to data list.");
                                mDataList.postLoading();
                            }
                            // query completed with snapshots
                            else {
                                List<Connection> allLists = snapshots.getDocuments().stream()
                                        .map(snapshot -> snapshot.toObject(Connection.class))
                                        .filter(Objects::nonNull)
                                        .collect(Collectors.toList());

                                mDataList.postSuccess(allLists);
                            }
                    });
        }

        return mDataList;
    }

    public StateMediatorLiveData<Connection> addUnique(Connection connection) {
        return new UniqueCreation(this, connection);
    }

    private StateLiveData<String> increaseCounter(String id) {
        // initialize output state live data with loading state
        StateModel<String> loadingState = new StateModel<>(StateStatus.LOADING);
        StateLiveData<String> response = new StateLiveData<>(loadingState);

        mColReference.document(id)
                .update("counter", FieldValue.increment(1))
                .addOnSuccessListener(aVoid -> {
                    // response post success with id of updated document
                    response.postSuccess(id);
                    Log.d(TAG, "Change connection counter: " + id);
                })
                .addOnFailureListener(e -> {
                    response.postError(e);
                    Log.e(TAG, "Failed to change document: " + id);
                });

        return response;
    }

    public static class UniqueCreation extends StateMediatorLiveData<Connection> {

        private ConnectionDAO mDAO;

        UniqueCreation(ConnectionDAO connectionDAO, Connection connection) {
            mDAO = connectionDAO;

            postLoading();

            StateMediatorLiveData<Connection> liveData = mDAO.read(connection.getId());

            if (liveData.getValue() != null
                    && liveData.getValue().getStatus() == StateStatus.SUCCESS) {
                increaseCounter(connection);
            }
            // can't get value
            else {
                addSource(liveData, stateModel -> {
                    switch (stateModel.getStatus()) {
                        case ERROR:
                            Exception err = stateModel.getError();

                            if (err instanceof DocumentNotFoundException) {
                                addNewDocument(connection);
                            } else postError(err);

                            break;

                        case LOADING:
                            postLoading();
                            break;

                        case SUCCESS:
                            removeSource(liveData);
                            increaseCounter(connection);
                            break;
                    }
                });
            }

            // keep mediator active until success
            observeForever(new Observer<StateModel<Connection>>() {
                @Override
                public void onChanged(StateModel<Connection> stateModel) {
                    switch (stateModel.getStatus()) {
                        case SUCCESS:
                            removeObserver(this);
                            break;
                    }
                }
            });
        }

        private void addNewDocument(Connection connection) {
            String connectionId = connection.getId();

            addSource(mDAO.add(connectionId, connection), stateModel -> {
                switch (stateModel.getStatus()) {
                    case ERROR:
                        postError(stateModel.getError());
                        break;

                    case LOADING:
                        postLoading();
                        break;

                    case SUCCESS:
                        postSuccess(stateModel.getData());
                        break;
                }
            });
        }

        private void increaseCounter(Connection connection) {
            String connectionId = connection.getId();

            addSource(mDAO.increaseCounter(connectionId), stateModel -> {
                switch (stateModel.getStatus()) {
                    case ERROR:
                        postError(stateModel.getError());
                        break;

                    case LOADING:
                        postLoading();
                        break;

                    case SUCCESS:
                        postSuccess(connection);
                        break;
                }
            });
        }
    }
}
