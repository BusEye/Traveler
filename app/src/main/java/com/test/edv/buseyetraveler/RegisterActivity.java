package com.test.edv.buseyetraveler;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {
  EditText Nametxt,Addrestxt,NICtxt,Emailtxt,TPtxt,Usernametxt,passwordtxt;
  RadioButton selectedrbtn;
  RadioGroup genderradioGroup;
  Button registerbtn;
  String Name,Addres,NIC,Email,Username,Password,Gender;
  int TP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Nametxt = (EditText) findViewById(R.id.NameText);
        Addrestxt = (EditText) findViewById(R.id.AddresText);
        NICtxt = (EditText) findViewById(R.id.NICText);
        Emailtxt = (EditText) findViewById(R.id.EmailText);
        TPtxt = (EditText) findViewById(R.id.PhoneText);
        Usernametxt = (EditText) findViewById(R.id.Usernametxt);
        passwordtxt = (EditText) findViewById(R.id.Passwordtxt);

        genderradioGroup =(RadioGroup) findViewById(R.id.gendergroup);

        registerbtn=(Button)findViewById(R.id.Registerbtn);

     //====================================================================

        registerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Name = Nametxt.getText().toString();
                Addres=Addrestxt.getText().toString();
                NIC=NICtxt.getText().toString();
                Email = Emailtxt.getText().toString();
                Username = Usernametxt.getText().toString();
                Password = passwordtxt.getText().toString();
                TP = Integer.parseInt(TPtxt.getText().toString());

                int Rbtnselectedid = genderradioGroup.getCheckedRadioButtonId();
                selectedrbtn = (RadioButton) findViewById(Rbtnselectedid);
                Gender = selectedrbtn.getText().toString();
            }
        });


        registerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNetworkRequest();
            }
        });




    }

    private  void  sendNetworkRequest() {

        try {
            RequestQueue requestqueue = Volley.newRequestQueue(RegisterActivity.this);

            String MoveURL ="https://buseye.000webhostapp.com/Reagister_user.php?Name="+Name+"&Address="+Addres+"&Gender="+Gender+"&NIC="+NIC+"&Email="+Email+"&TP="+TP+"&Username="+Username+"&Password="+Password;

            StringRequest stringrequest = new StringRequest(Request.Method.GET, MoveURL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {


                    try{ //{"Result":"true","ID":"1","Name":"Esandha"}

                        JSONObject jsonObj = new JSONObject(response);
                        Log.i("Full value",response);

                        String Result = jsonObj.getString("Massage")+" "+jsonObj.getString("ID");
                        Log.e("resalt",Result);

                        if (jsonObj.getString("Massage").equals("Sucsses"))
                        {
                            Toast.makeText(RegisterActivity.this,"Registration Sucssesfull",Toast.LENGTH_LONG).show();
                            try {
                                Intent k = new Intent(RegisterActivity.this, UserMenuActivity.class);
                                startActivity(k);
                            } catch(Exception e) {
                            }
                        }
                        else
                        {
                            Toast.makeText(RegisterActivity.this,"DetaBase Error",Toast.LENGTH_LONG).show();
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
