package com.techyourchance.testdrivendevelopment.exercise6.networking;

import com.techyourchance.testdrivendevelopment.exercise6.FetchUserUseCaseSync;
import com.techyourchance.testdrivendevelopment.exercise6.FetchUserUseCaseSync.UseCaseResult;
import com.techyourchance.testdrivendevelopment.exercise6.FetchUserUseCaseSyncImpl;
import com.techyourchance.testdrivendevelopment.exercise6.users.User;
import com.techyourchance.testdrivendevelopment.exercise6.users.UsersCache;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.omg.PortableInterceptor.SUCCESSFUL;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FetchUserHttpEndpointSyncTest {

    private static String USER_ID = "213";
    private static String USERNAME = "geros";

    @Mock
    UsersCache usersCacheMock;

    @Mock
    FetchUserHttpEndpointSync fetchUserHttpEndpointSync;

    FetchUserUseCaseSync SUT;

    /*
    1) If the user with given user ID is not in the cache then it should be fetched from the server.
    2) If the user fetched from the server then it should be stored in the cache before returning to the caller.
    3) If the user is in the cache then cached record should be returned without polling the server.
     */

    @Before
    public void setUp() throws Exception {
        SUT = new FetchUserUseCaseSyncImpl(fetchUserHttpEndpointSync, usersCacheMock);
    }

    // Fetch user, user not in cache, user id passed to endpoint
    // Fetch user, user in cache, user id not passed to endpoint
    //

    @Test
    public void fetchUserSync_notInCache_userIdPassedToEndpoint() throws NetworkErrorException {

        userNotInCache();
        successfulHttpEndpoint();

        SUT.fetchUserSync(USER_ID);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(fetchUserHttpEndpointSync, times(1))
                .fetchUserSync(captor.capture());
        verify(usersCacheMock, times(1)).getUser(captor.capture());
        assertThat(captor.getValue(), is(USER_ID));
        assertThat(captor.getValue(), is(USER_ID));

    }

    @Test
    public void fetchUserSync_userInCache_noPollingUserFromServer() throws NetworkErrorException {
        userInCache();
        SUT.fetchUserSync(USER_ID);
        verifyNoMoreInteractions(fetchUserHttpEndpointSync);
        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(usersCacheMock, times(1))
                .getUser(stringArgumentCaptor.capture());
    }

    @Test
    public void fetchUserSync_userInCache_returnCache() throws NetworkErrorException {

        userInCache();

        SUT.fetchUserSync(USER_ID);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

        verify(usersCacheMock, times(1))
                .getUser(captor.capture());
        assertThat(captor.getValue(), is(USER_ID));

    }

    // region Testing User Case Status --------------------------------------------------

    @Test
    public void fetchUserSync_success_successReturned() throws NetworkErrorException {
        successfulHttpEndpoint();
        UseCaseResult useCaseResult = SUT.fetchUserSync(USER_ID);
        assertThat(useCaseResult.getStatus(), is(FetchUserUseCaseSync.Status.SUCCESS));
    }

    @Test
    public void fetchUserSync_authError_failureReturned() throws NetworkErrorException {
        authErrorHttpEndpoint();
        UseCaseResult useCaseResult = SUT.fetchUserSync(USER_ID);
        assertThat(useCaseResult.getStatus(), is(FetchUserUseCaseSync.Status.FAILURE));
    }

    @Test
    public void fetchUserSync_generalError_failureReturned() throws NetworkErrorException {
        generalErrorHttpEndpoint();
        UseCaseResult useCaseResult = SUT.fetchUserSync(USER_ID);
        assertThat(useCaseResult.getStatus(), is(FetchUserUseCaseSync.Status.FAILURE));

    }

    @Test
    public void fetchUserSync_networkError_networkErrorThrown() throws NetworkErrorException {
        networkErrorHttpEndpoint();
        UseCaseResult useCaseResult = SUT.fetchUserSync(USER_ID);
        assertThat(useCaseResult.getStatus(), is(FetchUserUseCaseSync.Status.NETWORK_ERROR));

    }

    // endregion Testing User Case Status ------------------------------------------------------

    // region Testing User cache inter action

    @Test
    public void fetchUserSync_userNotInCacheAndSuccess_userCached() throws NetworkErrorException {
        successfulHttpEndpoint();
        userNotInCache();
        SUT.fetchUserSync(USER_ID);
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(usersCacheMock, times(1)).getUser(USER_ID);
        verify(usersCacheMock, times(1)).cacheUser(userArgumentCaptor.capture());
        assertThat(userArgumentCaptor.getValue().getUserId(), is(USER_ID));
    }

    @Test
    public void fetchUserSync_userNotInCacheAndAuthError_noUserCached() throws NetworkErrorException {
        authErrorHttpEndpoint();

        SUT.fetchUserSync(USER_ID);

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);

        verify(fetchUserHttpEndpointSync, times(1))
                .fetchUserSync(stringArgumentCaptor.capture());
        verify(usersCacheMock, times(1)).getUser(USER_ID);
        verify(usersCacheMock, times(0)).cacheUser(userArgumentCaptor.capture());
    }

    @Test
    public void fetchUserSync_generalError_noCacheInteraction() throws NetworkErrorException {
        generalErrorHttpEndpoint();
        SUT.fetchUserSync(USER_ID);
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);

        verify(fetchUserHttpEndpointSync, times(1))
                .fetchUserSync(stringArgumentCaptor.capture());
        verify(usersCacheMock, times(1)).getUser(USER_ID);
        verify(usersCacheMock, times(0)).cacheUser(userArgumentCaptor.capture());
    }

    @Test
    public void fetchUserSync_networkError_noUserCached() throws NetworkErrorException {
        networkErrorHttpEndpoint();
        SUT.fetchUserSync(USER_ID);

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);

        verify(fetchUserHttpEndpointSync, times(1))
                .fetchUserSync(stringArgumentCaptor.capture());
        verify(usersCacheMock, times(1)).getUser(USER_ID);
        verify(usersCacheMock, times(0)).cacheUser(userArgumentCaptor.capture());
    }

    // endregion Testing User cache inter action


    // region Cache mock -------------------------------------

    private void userNotInCache() {
        when(usersCacheMock.getUser(anyString()))
                .thenReturn(null);
    }

    private void userInCache() {
        when(usersCacheMock.getUser(USER_ID))
                .thenReturn(new User(USER_ID, USERNAME));
    }

    // endregion Cache mock ----------------------------------

    // region Http end point mocks -------------------------------------
    private void successfulHttpEndpoint() throws NetworkErrorException {
        when(fetchUserHttpEndpointSync.fetchUserSync(anyString()))
                .thenReturn(new FetchUserHttpEndpointSync.EndpointResult(
                        FetchUserHttpEndpointSync.EndpointStatus.SUCCESS, USER_ID, USERNAME));
    }

    private void generalErrorHttpEndpoint() throws NetworkErrorException {
        when(fetchUserHttpEndpointSync.fetchUserSync(anyString()))
                .thenReturn(new FetchUserHttpEndpointSync.EndpointResult(
                        FetchUserHttpEndpointSync.EndpointStatus.GENERAL_ERROR, null, null));
    }

    private void authErrorHttpEndpoint() throws NetworkErrorException {
        when(fetchUserHttpEndpointSync.fetchUserSync(anyString()))
                .thenReturn(new FetchUserHttpEndpointSync.EndpointResult(
                        FetchUserHttpEndpointSync.EndpointStatus.AUTH_ERROR, null, null));
    }

    private void networkErrorHttpEndpoint() throws NetworkErrorException {
        when(fetchUserHttpEndpointSync.fetchUserSync(anyString()))
                .thenThrow(new NetworkErrorException());
    }

    // endregion Http end point mocks ------------------------------------
}