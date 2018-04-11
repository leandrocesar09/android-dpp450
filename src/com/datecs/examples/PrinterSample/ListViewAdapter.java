package com.datecs.examples.PrinterSample;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.datecs.examples.PrinterSample.entity.Pedido;

public class ListViewAdapter extends BaseAdapter {
	List<Pedido> itens;
	Context context;
	
	public ListViewAdapter(Context context, List<Pedido> itens){
	        this.context = context;
	        this.itens = itens;
	    }
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return itens.size();
	}

	@Override
	public Object getItem(int position) {
		Pedido resultado = null;
		if (itens != null) {
            try {
                  resultado = itens.get(position);
            } catch (IndexOutOfBoundsException e) {
                  return null;
            }
      }
      return resultado;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		 Pedido pedido = itens.get(position);
         
         LayoutInflater inflater = (LayoutInflater)
                 context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
         
         if (convertView == null)
        	 convertView = inflater.inflate(R.layout.item_lista, null);

         TextView txtCliente = (TextView) convertView.findViewById(R.id.txtCliente);
         TextView txtCodigoPedido = (TextView) convertView.findViewById(R.id.txtCodigoPedido);
         TextView txtValor = (TextView) convertView.findViewById(R.id.txtValor);
         
         txtCliente.setText(pedido.getCodcliente_pe()+"-"+pedido.getCliente());
         txtCodigoPedido.setText(pedido.getCod_pe());
         txtValor.setText(pedido.getDatavenda_pe());
         
         return convertView;
	}

}
