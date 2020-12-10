package com.catata.sqlitepersonaslist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.catata.sqlitepersonaslist.model.Persona;
import com.catata.sqlitepersonaslist.sqlite.SQLManager;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

public class MainActivity extends AppCompatActivity {
    EditText etNombre, etApellidos, etEdad;
    Button btnAdd;
    RecyclerView lista;
    MyAdapter myAdapter;

    List<Persona> listaPersonas;

    SQLManager SQLManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SQLManager = new SQLManager(this);

        etApellidos = (EditText) findViewById(R.id.etApellido);
        etNombre = (EditText) findViewById(R.id.etNombre);
        etEdad = (EditText) findViewById(R.id.etEdad);
        btnAdd = (Button)findViewById(R.id.btnAdd);

        lista = (RecyclerView)findViewById(R.id.lista);

        listaPersonas = SQLManager.selectAll();

        myAdapter = new MyAdapter(listaPersonas,this);

        lista.setLayoutManager(new LinearLayoutManager(this));

        lista.setAdapter(myAdapter);



    }

    @Override
    protected void onDestroy() {
        SQLManager.cerrar();
        super.onDestroy();
    }

    public void addPersona(View view) {

        if (TextUtils.isEmpty(etNombre.getText().toString()) ||
                TextUtils.isEmpty(etApellidos.getText().toString()) ||
                TextUtils.isEmpty(etEdad.getText().toString())){

            Toast.makeText(MainActivity.this, getString(R.string.empty_fields),
            Toast.LENGTH_SHORT).show();

        }else{
            String nombre = etNombre.getText().toString();
            String apellidos = etApellidos.getText().toString();
            int edad = Integer.parseInt(etEdad.getText().toString());

            Persona p = new Persona(-1,nombre,apellidos,edad);

            p.setId(SQLManager.insert(p));

            myAdapter.addPersona(p);

            limparTextos();

            etNombre.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(etNombre.getWindowToken(), 0);
        }



    }

    private void limparTextos() {
        etNombre.setText("");
        etApellidos.setText("");
        etEdad.setText("");
    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>{
        List<Persona> listaPersonas;
        Context c;

        public MyAdapter(List<Persona> listaPersonas, Context c) {
            super();
            this.listaPersonas = listaPersonas;
            this.c = c;
        }

        public void addPersona(Persona p){
            listaPersonas.add(p);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_lista, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            holder.tvNombre.setText(listaPersonas.get(position).getNombre());
            holder.tvApellidos.setText(listaPersonas.get(position).getApellidos());
            holder.tvEdad.setText(""+listaPersonas.get(position).getEdad());

            holder.btnBorrar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(SQLManager.deleteById(listaPersonas.get(position).getId())==1){
                        listaPersonas.remove(position);
                        notifyDataSetChanged();
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return listaPersonas.size();
        }

        private class MyViewHolder extends RecyclerView.ViewHolder {
            TextView tvNombre, tvApellidos, tvEdad;
            ImageButton btnBorrar;
            public MyViewHolder(View v){
                super(v);
                tvNombre = (TextView)v.findViewById(R.id.tvNombre);
                tvApellidos = (TextView)v.findViewById(R.id.tvApellidos);
                tvEdad = (TextView)v.findViewById(R.id.tvEdad);
                btnBorrar = (ImageButton)v.findViewById(R.id.btnBorrar);
            }

        }
    }
}