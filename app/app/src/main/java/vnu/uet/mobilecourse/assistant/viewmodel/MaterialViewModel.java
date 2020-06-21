package vnu.uet.mobilecourse.assistant.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import vnu.uet.mobilecourse.assistant.model.material.MaterialContent;
import vnu.uet.mobilecourse.assistant.repository.course.MaterialRepository;

public class MaterialViewModel extends ViewModel {

    private MaterialRepository materialRepo = MaterialRepository.getInstance();

    public LiveData<? extends MaterialContent> getDetailContent(int id, String type) {
        return materialRepo.getDetails(id, type);
    }
}
