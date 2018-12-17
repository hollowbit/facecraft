package uk.co.olbois.facecraft.tasks;

import android.os.AsyncTask;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import io.grpc.Server;
import uk.co.olbois.facecraft.model.SampleUser;
import uk.co.olbois.facecraft.model.serverconnection.ServerConnection;
import uk.co.olbois.facecraft.server.Either;
import uk.co.olbois.facecraft.server.HttpRequestBuilder;
import uk.co.olbois.facecraft.server.HttpResponse;
import uk.co.olbois.facecraft.server.OnResponseListener;
import uk.co.olbois.facecraft.server.ServerException;

public class RetrieveUserServersTask extends AsyncTask<Void, Void, Either<Exception, List<ServerConnection>>> {
    private OnResponseListener<List<ServerConnection>> onResponseListener;
    private String path;

    public RetrieveUserServersTask(String path, OnResponseListener<List<ServerConnection>> onResponseListener){
        this.onResponseListener = onResponseListener;
        this.path = path;
    }

    @Override
    protected Either<Exception, List<ServerConnection>> doInBackground(Void... voids) {

        try {
            HttpResponse response = new HttpRequestBuilder(path + "/serversOwned")
                    .method(HttpRequestBuilder.Method.GET)
                    .perform();

            String json = new String(response.getContent(), "UTF8");
            ServerConnection[] serversOwned = ServerConnection.parseArray(json);

            response = new HttpRequestBuilder(path + "/serversPartOf")
                    .method(HttpRequestBuilder.Method.GET)
                    .perform();

            json = new String(response.getContent(), "UTF8");
            ServerConnection[] serversMember = ServerConnection.parseArray(json);


            for(ServerConnection c : serversOwned){
                c.setRole(ServerConnection.Role.OWNER);
                HttpResponse r = new HttpRequestBuilder("/servers/" + c.getId() + "/owners")
                        .method(HttpRequestBuilder.Method.GET)
                        .perform();

                String jsn = new String(r.getContent(), "UTF8");
                SampleUser[] owners = SampleUser.parseArray(jsn);

                r = new HttpRequestBuilder("/servers/" + c.getId() + "/members")
                        .method(HttpRequestBuilder.Method.GET)
                        .perform();

                jsn = new String(r.getContent(), "UTF8");
                SampleUser[] members = SampleUser.parseArray(jsn);

                c.setUserCount(owners.length + members.length);
            }

            for(ServerConnection c : serversMember){
                c.setRole(ServerConnection.Role.MEMBER);
                HttpResponse r = new HttpRequestBuilder("/servers/" + c.getId() + "/owners")
                        .method(HttpRequestBuilder.Method.GET)
                        .perform();

                String jsn = new String(r.getContent(), "UTF8");
                SampleUser[] owners = SampleUser.parseArray(jsn);

                r = new HttpRequestBuilder("/servers/" + c.getId() + "/members")
                        .method(HttpRequestBuilder.Method.GET)
                        .perform();

                jsn = new String(r.getContent(), "UTF8");
                SampleUser[] members = SampleUser.parseArray(jsn);

                c.setUserCount(owners.length + members.length);
            }

            List<ServerConnection> servers = new LinkedList<>();
            servers.addAll(Arrays.asList(serversOwned));
            servers.addAll(Arrays.asList(serversMember));

            return Either.right(servers);
        } catch (IOException | ServerException e) {
            return Either.left(e);
        }
    }

    @Override
    protected void onPostExecute(Either<Exception, List<ServerConnection>> either) {
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
