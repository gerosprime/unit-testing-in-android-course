package com.techyourchance.testdrivendevelopment.exercise6;

import com.techyourchance.testdrivendevelopment.exercise6.networking.FetchUserHttpEndpointSync;
import com.techyourchance.testdrivendevelopment.exercise6.networking.NetworkErrorException;
import com.techyourchance.testdrivendevelopment.exercise6.users.User;
import com.techyourchance.testdrivendevelopment.exercise6.users.UsersCache;

public class FetchUserUseCaseSyncImpl implements FetchUserUseCaseSync {

    FetchUserHttpEndpointSync fetchUserHttpEndpointSync;
    UsersCache mUsersCacheMock;

    public FetchUserUseCaseSyncImpl(FetchUserHttpEndpointSync fetchUserHttpEndpointSync,
                                    UsersCache mUsersCacheMock) {
        this.fetchUserHttpEndpointSync = fetchUserHttpEndpointSync;
        this.mUsersCacheMock = mUsersCacheMock;
    }

    @Override
    public UseCaseResult fetchUserSync(String userId) {

        FetchUserHttpEndpointSync.EndpointResult endpointResult;
        try {

            User user = mUsersCacheMock.getUser(userId);
            if (user == null)
                endpointResult = fetchUserHttpEndpointSync.fetchUserSync(userId);
            else
                return new UseCaseResult(Status.SUCCESS, user);

        } catch (NetworkErrorException e) {
            return new UseCaseResult(Status.NETWORK_ERROR, null);
        }

        if (FetchUserHttpEndpointSync.EndpointStatus.SUCCESS.equals(endpointResult.getStatus())) {

            User user = new User(endpointResult.getUserId(),
                    endpointResult.getUsername());

            mUsersCacheMock.cacheUser(user);

            return new UseCaseResult(Status.SUCCESS, user);
        } else {
            return new UseCaseResult(Status.FAILURE, null);
        }
    }
}
