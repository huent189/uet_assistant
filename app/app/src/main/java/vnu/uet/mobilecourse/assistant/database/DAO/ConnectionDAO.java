package vnu.uet.mobilecourse.assistant.database.DAO;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
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
//                    .orderBy("deadline", Query.Direction.DESCENDING)
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

    public StateMediatorLiveData<Connection> addUnique(String id, Connection connection) {
//        StateMediatorLiveData<Connection> liveData = new StateMediatorLiveData<>();
//        liveData.postLoading();
//
//        liveData.addSource(mDataList, new Observer<StateModel<List<Connection>>>() {
//            @Override
//            public void onChanged(StateModel<List<Connection>> stateModel) {
//                switch (stateModel.getStatus()) {
//                    case ERROR:
//                        liveData.postError(stateModel.getError());
//                        break;
//
//                    case LOADING:
//                        liveData.postLoading();
//                        break;
//
//                    case SUCCESS:
//                        liveData.removeSource(mDataList);
//
//                        boolean isExist = stateModel.getData().stream()
//                                .anyMatch(conn -> conn.getId().equals(id));
//
//                        if (!isExist) {
//                            liveData.addSource(add(id, connection), new Observer<StateModel<Connection>>() {
//                                @Override
//                                public void onChanged(StateModel<Connection> stateModel) {
//                                    switch (stateModel.getStatus()) {
//                                        case ERROR:
//                                            liveData.postError(stateModel.getError());
//                                            break;
//
//                                        case LOADING:
//                                            liveData.postLoading();
//                                            break;
//
//                                        case SUCCESS:
//                                            liveData.postSuccess(stateModel.getData());
//                                            break;
//                                    }
//                                }
//                            });
//                        }
//
//                }
//            }
//        });
//
//        return liveData;

        return new UniqueCreation(this, connection);
    }

    public static class UniqueCreation extends StateMediatorLiveData<Connection> {

        private ConnectionDAO mDAO;

        public UniqueCreation(ConnectionDAO connectionDAO, Connection connection) {
            mDAO = connectionDAO;

            postLoading();

            StateLiveData<List<Connection>> listLiveData = mDAO.readAll();
            if (listLiveData.getValue() != null
                    && listLiveData.getValue().getStatus() == StateStatus.SUCCESS) {
                checkList(listLiveData.getValue().getData(), connection);
            } else {
                addSource(connectionDAO.readAll(), new Observer<StateModel<List<Connection>>>() {
                    @Override
                    public void onChanged(StateModel<List<Connection>> stateModel) {
                        switch (stateModel.getStatus()) {
                            case ERROR:
                                postError(stateModel.getError());
                                break;

                            case LOADING:
                                postLoading();
                                break;

                            case SUCCESS:
                                removeSource(connectionDAO.readAll());
                                checkList(stateModel.getData(), connection);
                                break;
                        }
                    }
                });
            }
        }

        private void checkList(List<Connection> list, Connection connection) {
            boolean isExist = list.stream()
                    .anyMatch(conn -> conn.getId().equals(connection.getId()));

            if (!isExist) {
                addSource(mDAO.add(connection.getId(), connection), new Observer<StateModel<Connection>>() {
                    @Override
                    public void onChanged(StateModel<Connection> stateModel) {
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
                    }
                });
            }
        }
    }
}
