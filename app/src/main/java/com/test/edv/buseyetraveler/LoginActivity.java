package com.test.edv.buseyetraveler;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button btnlogin = (Button)findViewById(R.id.btnlogin);
        Button btnregister =(Button)findViewById(R.id.btnRequestRegister);
        final EditText txtuserName =(EditText)findViewById(R.id.txtuserName);
        final EditText txtPassword =(EditText)findViewById(R.id.txtPassword);

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName =txtuserName.getText().toString();
                String Password =txtPassword.getText().toString();
               sendNetworkRequest(userName,Password);

            }
        });


        btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent register = new Intent(LoginActivity.this, RegisterActivity.class);
                    startActivity(register);
                } catch(Exception e) {
                }
            }
        });

    }

    private  void  sendNetworkRequest(String name, String pass) {

        try {
            RequestQueue requestqueue = Volley.newRequestQueue(LoginActivity.this);

            String MoveURL ="https://buseye.000webhostapp.com/login_user.php?UserName="+name+"&Password="+pass;

            StringRequest stringrequest = new StringRequest(Request.Method.GET, MoveURL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {


                    try{ //{"Result":"true","ID":"1","Name":"Esandha"}

                        JSONObject jsonObj = new JSONObject(response);
                        Log.i("Full value",response);

                        String Result = jsonObj.getString("Result")+" "+jsonObj.getString("ID")+" "+jsonObj.getString("Name");
                        Log.e("resalt",Result);

                        if (jsonObj.getString("Result").equals("true"))
                        {
                            try {
                                Intent usermenu = new Intent(LoginActivity.this, UserMenuActivity.class);
                                startActivity(usermenu);
                            } catch(Exception e) {
                            }
                        }
                        else
                        {
                            Toast.makeText(LoginActivity.this,"Plese check user name and password",Toast.LENGTH_LONG).show();
                        }


                    }
                    catch (JSONException e)
                    {
                        Log.e("ls", "Json parsing error: " + e);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    Log.e("Error", error.toString());
                }
            });
            requestqueue.add(stringrequest);
        }
        catch (Exception e)
        {
            Log.e("EX", "Exception: " + e.getMessage());
        }
    }

}
