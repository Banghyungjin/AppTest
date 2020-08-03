package com.example.apptest;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Button explorerButton = (Button)findViewById(R.id.explorerBtn);
        explorerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ExplorerActivity.class);
                startActivity(intent);
            }
        });

        Button mapButton = (Button)findViewById(R.id.mapBtn);
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"Sorry I'm still working on it",Toast.LENGTH_LONG).show();
            }
        });
    }

}