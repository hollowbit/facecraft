package uk.co.olbois.facecraft.tasks;


import android.os.AsyncTask;
import android.widget.Toast;

import java.io.IOException;

import io.grpc.Server;
import uk.co.olbois.facecraft.model.SampleUser;
import uk.co.olbois.facecraft.server.Either;
import uk.co.olbois.facecraft.server.HttpRequestBuilder;
import uk.co.olbois.facecraft.server.HttpResponse;
import uk.co.olbois.facecraft.server.OnResponseListener;
import uk.co.olbois.facecraft.server.ServerException;

public class ValidateLoginTask extends AsyncTask<String, Void, Either<Exception, SampleUser>> {
    private String path;
    private OnResponseListener<SampleUser> onResponseListener;
    private String username;
    private String password;

    public ValidateLoginTask(String path, String username, String password, OnResponseListener<SampleUser> onResponseListener){
        this.path = path;
        this.username = username;
        this.password = password;
        this.onResponseListener = onResponseListener;
    }

    @Override
    protected Either<Exception, SampleUser> doInBackground(String... tokens) {
        String token = tokens[0];
        try {
            HttpResponse response = new HttpRequestBuilder(path)
                    .method(HttpRequestBuilder.Method.GET)
                    .perform();

            String json = new String(response.getContent(), "UTF8");
            SampleUser[] springUsers = SampleUser.parseArray(json);

            for(SampleUser s : springUsers){
                if(s.getUsername().toLowerCase().equals(username) && s.getPassword().equals(password)){
                    s.setDeviceToken(token);

                    HttpResponse response2 = new HttpRequestBuilder(path + "/" + s.getId())
                            .method(HttpRequestBuilder.Method.PUT)
                            .withRequestBody("application/json", s.format().getBytes())
                            .perform();

                    return Either.right(s);
                }
            }
        } catch (IOException | ServerException e) {
            return Either.left(e);
        }
        return Either.left(new IOException("No user with that password or username!"));
    }

    @Override
    protected void onPostExecute(Either<Exception, SampleUser> either) {
        if(onResponseListener == null)
            return;

        switch(either.getType()){
            case LEFT:
                onResponseListener.onError(either.getLeft());
                break;
            case RIGHT:
                onResponseListener.onResponse(either.getRight());
                break;
        }
    }

}
