package de.snertlab.discoserv;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import de.snertlab.discoserv.model.IPosition;

public class DiscotelPositionArrayAdapter extends ArrayAdapter<IPosition>{
	
	private int resource;
	
	public DiscotelPositionArrayAdapter(Context context, int resource, List<IPosition> items) {
		super(context, resource, items);
		this.resource = resource;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout positionView;
		IPosition positionObj = getItem(position);
		
		if(convertView==null){
			positionView = new LinearLayout(getContext());
			String inflater = Context.LAYOUT_INFLATER_SERVICE;
			LayoutInflater vi;
			vi = (LayoutInflater) getContext().getSystemService(inflater);
			vi.inflate(resource, positionView, true);
		}else{
			positionView = (LinearLayout) convertView;
		}
		TextView txtView = (TextView)positionView.findViewById(R.id.textView1);
		TextView txtView2 = (TextView)positionView.findViewById(R.id.textView2);
		txtView.setText(positionObj.getPositionBez());
		txtView2.setText(positionObj.getBrutto() +"Û");
		return positionView;
	}
}
