package uk.co.olbois.facecraft.tasks;

import android.os.AsyncTask;

import java.io.IOException;

import uk.co.olbois.facecraft.model.SampleUser;
import uk.co.olbois.facecraft.server.Either;
import uk.co.olbois.facecraft.server.HttpRequestBuilder;
import uk.co.olbois.facecraft.server.HttpResponse;
import uk.co.olbois.facecraft.server.OnResponseListener;
import uk.co.olbois.facecraft.server.ServerException;

public class RegisterUserTask extends AsyncTask<String, Void, Either<Exception, SampleUser> > {

    private String path;
    private OnResponseListener<SampleUser> onResponseListener;
    private String username;
    private String password;

    public RegisterUserTask(String path, String username, String password, OnResponseListener<SampleUser> onResponseListener){
        this.path = path;
        this.password = password;
        this.username = username;
        this.onResponseListener = onResponseListener;
    }

    @Override
    protected Either<Exception, SampleUser> doInBackground(String... tokens) {
        String token = tokens[0];

        SampleUser newUser = new SampleUser();
        newUser.setUsername(username);
        newUser.setPassword(password);
        newUser.setDeviceToken(token);

        try {
            //Retrieve all users
            HttpResponse response = new HttpRequestBuilder(path)
                    .method(HttpRequestBuilder.Method.GET)
                    .perform();

            String json = new String(response.getContent(), "UTF8");
            SampleUser[] springUsers = SampleUser.parseArray(json);

            //Check to see if the user's entered credentials match with a record in the database
            for(SampleUser s : springUsers){
                if(s.getUsername().toLowerCase().equals(newUser.getUsername().toLowerCase())){
                    return Either.left(new IOException("That user already exists!"));
                }
            }

            //user doesn't exist!
            response = new HttpRequestBuilder(path)
                    .method(HttpRequestBuilder.Method.POST)
                    .withRequestBody("application/json", newUser.format().getBytes())
                    .perform();

            //Get the location of the new user
            String locationFields[] = response.getHeaderFields().get("Location").get(0).split("/");
            String id = locationFields[locationFields.length-1];

            //Read the new user from database
            response = new HttpRequestBuilder(path + "/" + id)
                    .method(HttpRequestBuilder.Method.GET)
                    .perform();

            //Sanity check to make sure the user was retrieved properly
            if(response.getCode() == 404){
                return Either.left(new IOException("The user doesn't exist in the database!"));
            }

            //Make user from database into a sampleuser
            json = new String(response.getContent(), "UTF8");
            newUser = SampleUser.parse(json);


            return Either.right(newUser);
        } catch (IOException | ServerException e) {
            return Either.left(e);
        }
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
