package vnu.uet.mobilecourse.assistant.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.adapter.viewholder.MaterialViewHolder;
import vnu.uet.mobilecourse.assistant.model.Material;

public class CourseGeneralMaterialAdapter extends RecyclerView.Adapter<MaterialViewHolder> {

    private List<Material> mMaterials;
    private Fragment mOwner;
    private NavController mNavController;


    public CourseGeneralMaterialAdapter(List<Material> materials, Fragment owner) {
        mMaterials = materials;
        mOwner = owner;

        Activity activity = owner.getActivity();

        if (activity != null) {
            mNavController = Navigation
                    .findNavController(activity, R.id.nav_host_fragment);
        }
    }

    @NonNull
    @Override
    public MaterialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mOwner.getLayoutInflater()
                .inflate(R.layout.layout_material_item, parent, false);

        return new MaterialViewHolder(view, mNavController);
    }

    @Override
    public void onBindViewHolder(@NonNull MaterialViewHolder holder, int position) {
        final Material material = mMaterials.get(position);
        holder.bind(material);
    }

    @Override
    public int getItemCount() {
        return mMaterials.size();
    }
}
