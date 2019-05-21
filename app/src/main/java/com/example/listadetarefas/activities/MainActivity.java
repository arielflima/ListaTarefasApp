package com.example.listadetarefas.activities;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.listadetarefas.R;
import com.example.listadetarefas.adapters.TarefaAdapter;
import com.example.listadetarefas.helper.DbHelper;
import com.example.listadetarefas.helper.RecyclerItemClickListener;
import com.example.listadetarefas.helper.TarefaDAO;
import com.example.listadetarefas.model.Tarefa;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TarefaAdapter tarefaAdapter;
    private List<Tarefa> listaTarefas = new ArrayList<>();
    private Tarefa tarefaSelecionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //configurar recycler
        recyclerView = findViewById(R.id.recyclerView);

        DbHelper db = new DbHelper(getApplicationContext());

        ContentValues cv = new ContentValues();
        cv.put("nome", "teste");

        db.getWritableDatabase().insert("tarefas", null, cv);


        //adicionando evento de click
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(),
                recyclerView,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                        //recupera tarefa pra edicao
                        Tarefa tarefaSelecionada = listaTarefas.get(position);


                        //envia intent para tela de adicionar
                        Intent intent = new Intent(MainActivity.this, AdicionarTarefaActivity.class);
                        intent.putExtra("tarefaSelecionada", tarefaSelecionada);

                        startActivity(intent);

                    }

                    @Override
                    public void onItemClick(View view, int position) {

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                        //Recuperar tarefa que o usuario quer deletar
                        tarefaSelecionada = listaTarefas.get(position);


                        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);

                        //Configurar o tipo de msg
                        dialog.setTitle("Confirmar exclusão?");
                        dialog.setMessage("Deseja excluir a tarefa: " + tarefaSelecionada.getNomeTarefa() + " ?");

                        dialog.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                TarefaDAO tarefaDAO = new TarefaDAO(getApplicationContext());
                                if (tarefaDAO.deletar(tarefaSelecionada)){
                                    carregarListaTarefas();
                                    Toast.makeText(getApplicationContext(), "Sucesso ao excluir tarefa!", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(getApplicationContext(), "Erro ao excluir tarefa!", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
                        dialog.setNegativeButton("Não", null);

                        //exibir dialog
                        dialog.create();
                        dialog.show();
                    }
                }));


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AdicionarTarefaActivity.class);
                startActivity(intent);
            }
        });
    }

    public void carregarListaTarefas() {
        //listar tarefas
        TarefaDAO tarefaDAO = new TarefaDAO(getApplicationContext());
        listaTarefas = tarefaDAO.listar();

        //configurar adapter
        tarefaAdapter = new TarefaAdapter(listaTarefas);

        //configurar recyclerview
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayout.VERTICAL));
        recyclerView.setAdapter(tarefaAdapter);
    }

    @Override
    protected void onStart() {
        carregarListaTarefas();
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
