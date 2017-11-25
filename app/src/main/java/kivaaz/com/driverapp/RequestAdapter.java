package kivaaz.com.driverapp;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Muguntan on 11/25/2017.
 */

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.myViewHolder> {

    private LayoutInflater inflater;
    List<RequestList> data = new ArrayList<>();
    Context context;
    private OnItemClick mCallback;

    public RequestAdapter(List<RequestList> data, Context context, OnItemClick mCallback) {
        this.data = data;
        this.context = context;
        this.mCallback = mCallback;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.request_adapter,null);
        myViewHolder holder = new myViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(myViewHolder holder, int position) {
        if(data.get(position).getReqAccepted()){
            holder.accepted_btn.setText("Accepted");
            holder.accepted_btn.setBackgroundColor(Color.parseColor("#972D2D"));
        }
        holder.name.setText(data.get(position).getReqName());
        holder.desc.setText(data.get(position).getReqDesc());
        holder.email.setText(data.get(position).getReqEmail());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder{
        TextView name, desc, email, accepted_btn;
        public myViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.req_nameTV);
            desc = itemView.findViewById(R.id.req_descTV);
            email = itemView.findViewById(R.id.req_emailTV);
            accepted_btn = itemView.findViewById(R.id.accept_btn);

            accepted_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getLayoutPosition();
                    String reqName = data.get(position).getReqName();
                    String reqEmail = data.get(position).getReqEmail();
                    accepted_btn.setText("Accepted");
                    accepted_btn.setBackgroundColor(Color.parseColor("#972D2D"));
                    mCallback.OnClick(reqName,reqEmail);
                }
            });
        }
    }

    interface OnItemClick {
        void OnClick(String reqname, String reqemail);
    }
}