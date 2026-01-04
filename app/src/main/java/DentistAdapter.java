import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class DentistAdapter extends RecyclerView.Adapter<DentistAdapter.ViewHolder> {

    List<Dentist> dentistList;

    public DentistAdapter(List<Dentist> dentistList) {
        this.dentistList = dentistList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dentist_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Dentist d = dentistList.get(position);

        holder.name.setText(d.name);
        holder.qualification.setText(d.qualification);
        holder.phone.setText("Phone: " + d.phone);
        holder.email.setText("Email: " + d.email);
        holder.state.setText("State: " + d.state);
        holder.image.setImageResource(d.image);
    }

    @Override
    public int getItemCount() {
        return dentistList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, qualification, phone, email, state;
        ImageView image;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.txtName);
            qualification = itemView.findViewById(R.id.txtQualification);
            phone = itemView.findViewById(R.id.txtPhone);
            email = itemView.findViewById(R.id.txtEmail);
            state = itemView.findViewById(R.id.txtState);
            image = itemView.findViewById(R.id.imgDentist);
        }
    }
}