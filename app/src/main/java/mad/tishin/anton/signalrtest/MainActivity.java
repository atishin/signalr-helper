package mad.tishin.anton.signalrtest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.gson.JsonElement;
import com.tishinanton.signalrhelper.SignalRServiceHelper;

import java.util.ArrayList;

import mad.tishin.anton.signalrtest.Model.ChatMessage;
import microsoft.aspnet.signalr.client.Action;

public class MainActivity extends AppCompatActivity {


    private Button sendButton;
    private EditText messageText;
    private ArrayList<ChatMessage> messages;
    private ListView listView;
    private ArrayAdapter<ChatMessage> adapter;

    private SignalRServiceHelper connectionHelper;

    private static final String signalRHostUrl = "http://10.0.2.12/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        messages = new ArrayList<ChatMessage>();
        adapter = new ArrayAdapter<ChatMessage>(this, android.R.layout.simple_expandable_list_item_1, messages);
        listView = (ListView)findViewById(R.id.listView);
        listView.setAdapter(adapter);

        messageText = (EditText)findViewById(R.id.messageText);

        sendButton = (Button)findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectionHelper.invoke("SendMessage", messageText.getText().toString(), "Android");
            }
        });


        connectionHelper = new SignalRServiceHelper(signalRHostUrl, "ChatHub");
        connectionHelper.subscribe("newMessage", new Action<JsonElement[]>() {
            @Override
            public void run(JsonElement[] jsonElements) throws Exception {
                String author = jsonElements[1].getAsString();
                String message = jsonElements[0].getAsString();
                messages.add(new ChatMessage(message, author));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                        listView.setSelection(adapter.getCount() - 1);
                    }
                });
            }
        });
        connectionHelper.start();
    }
}
