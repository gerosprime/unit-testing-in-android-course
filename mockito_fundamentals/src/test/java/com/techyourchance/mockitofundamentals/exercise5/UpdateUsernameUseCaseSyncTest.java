package com.techyourchance.mockitofundamentals.exercise5;

import com.techyourchance.mockitofundamentals.exercise5.eventbus.EventBusPoster;
import com.techyourchance.mockitofundamentals.exercise5.eventbus.UserDetailsChangedEvent;
import com.techyourchance.mockitofundamentals.exercise5.networking.NetworkErrorException;
import com.techyourchance.mockitofundamentals.exercise5.networking.UpdateUsernameHttpEndpointSync;
import com.techyourchance.mockitofundamentals.exercise5.users.User;
import com.techyourchance.mockitofundamentals.exercise5.users.UsersCache;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class UpdateUsernameUseCaseSyncTest {

    static String USER_ID = "123";
    static String USERNAME = "geros";

    UpdateUsernameUseCaseSync SUT;

    UpdateUsernameHttpEndpointSync usernameHttpEndpointSync;
    UsersCache usersCache;
    EventBusPoster eventBusPoster;

    private User user;

    @Before
    public void setUp() throws Exception {
        usernameHttpEndpointSync = Mockito.mock(UpdateUsernameHttpEndpointSync.class);
        usersCache = Mockito.mock(UsersCache.class);
        eventBusPoster = Mockito.mock(EventBusPoster.class);
        SUT = new UpdateUsernameUseCaseSync(usernameHttpEndpointSync,
                usersCache, eventBusPoster);

        user = new User(USER_ID, USERNAME);

    }

    @Test
    public void updateUser_successUpdate_userCached() throws NetworkErrorException {
        usernameUpdateHttpEndpointSuccess();
        SUT.updateUsernameSync(USER_ID, USERNAME);

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        Mockito.verify(usersCache, times(1))
                .cacheUser(userArgumentCaptor.capture());

        Assert.assertThat(userArgumentCaptor.getValue().getUserId(), is(USER_ID));
        Assert.assertThat(userArgumentCaptor.getValue().getUsername(), is(USERNAME));

    }

    @Test
    public void updateUser_generalError_userCacheNoInteraction() throws NetworkErrorException {
        usernameUpdateHttpEndpointGeneralError();
        SUT.updateUsernameSync(USER_ID, USERNAME);
        verifyNoMoreInteractions(usersCache);
    }

    @Test
    public void updateUser_authError_userCacheNoInteraction() throws NetworkErrorException {
        usernameUpdateHttpEndpointAuthError();
        SUT.updateUsernameSync(USER_ID, USERNAME);
        verifyNoMoreInteractions(usersCache);
    }

    @Test
    public void updateUser_serverError_userCacheNoInteraction() throws NetworkErrorException {
        usernameUpdateHttpEndpointServerError();
        SUT.updateUsernameSync(USER_ID, USERNAME);
        verifyNoMoreInteractions(usersCache);
    }

    @Test
    public void updateUser_networkError_userCacheNoInteraction() throws NetworkErrorException {
        usernameUpdateHttpEndpointNetworkError();
        SUT.updateUsernameSync(USER_ID, USERNAME);
        verifyNoMoreInteractions(usersCache);
    }

    // SUT Updates user successfully,
    // SUT Update user general error, failure returned
    // SUT Update user server error, failure returned
    // SUT Update user auth error, failure returned
    // SUT Update user network error, network error returned.

    @Test
    public void updateUser_successUpdate_successReturned() throws NetworkErrorException {

        usernameUpdateHttpEndpointSuccess();
        UpdateUsernameUseCaseSync.UseCaseResult useCaseResult
                = SUT.updateUsernameSync(USER_ID, USERNAME);
        assertThat(useCaseResult, is(UpdateUsernameUseCaseSync.UseCaseResult.SUCCESS));

    }

    @Test
    public void updateUser_serverError_failureReturned() throws NetworkErrorException {

        usernameUpdateHttpEndpointServerError();
        UpdateUsernameUseCaseSync.UseCaseResult useCaseResult
                = SUT.updateUsernameSync(USER_ID, USERNAME);
        assertThat(useCaseResult, is(UpdateUsernameUseCaseSync.UseCaseResult.FAILURE));

    }

    @Test
    public void updateUser_authError_failureReturned() throws NetworkErrorException {

        usernameUpdateHttpEndpointAuthError();
        UpdateUsernameUseCaseSync.UseCaseResult useCaseResult
                = SUT.updateUsernameSync(USER_ID, USERNAME);
        assertThat(useCaseResult, is(UpdateUsernameUseCaseSync.UseCaseResult.FAILURE));

    }

    @Test
    public void updateUser_generalError_failureReturned() throws NetworkErrorException {

        usernameUpdateHttpEndpointGeneralError();
        UpdateUsernameUseCaseSync.UseCaseResult useCaseResult
                = SUT.updateUsernameSync(USER_ID, USERNAME);
        assertThat(useCaseResult, is(UpdateUsernameUseCaseSync.UseCaseResult.FAILURE));

    }

    @Test
    public void updateUser_successUpdate_networkError() throws NetworkErrorException {

        usernameUpdateHttpEndpointNetworkError();
        UpdateUsernameUseCaseSync.UseCaseResult useCaseResult
                = SUT.updateUsernameSync(USER_ID, USERNAME);
        assertThat(useCaseResult, is(UpdateUsernameUseCaseSync.UseCaseResult.NETWORK_ERROR));

    }

    @Test
    public void updateUser_success_eventPosted() throws NetworkErrorException {

        ArgumentCaptor<Object> objectArgumentCaptor = ArgumentCaptor.forClass(Object.class);
        usernameUpdateHttpEndpointSuccess();
        UpdateUsernameUseCaseSync.UseCaseResult useCaseResult
                = SUT.updateUsernameSync(USER_ID, USERNAME);

        verify(eventBusPoster, times(1))
                .postEvent(objectArgumentCaptor.capture());
        assertThat(objectArgumentCaptor.getValue(), is(instanceOf(UserDetailsChangedEvent.class)));

    }

    @Test
    public void updateUser_authError_eventPostNoInteraction() throws NetworkErrorException {

        usernameUpdateHttpEndpointAuthError();
        UpdateUsernameUseCaseSync.UseCaseResult useCaseResult
                = SUT.updateUsernameSync(USER_ID, USERNAME);
        verifyNoMoreInteractions(eventBusPoster);

    }

    @Test
    public void updateUser_generalError_eventPostNoInteraction() throws NetworkErrorException {

        usernameUpdateHttpEndpointGeneralError();
        UpdateUsernameUseCaseSync.UseCaseResult useCaseResult
                = SUT.updateUsernameSync(USER_ID, USERNAME);
        verifyNoMoreInteractions(eventBusPoster);

    }

    @Test
    public void updateUser_serverError_eventPostNoInteraction() throws NetworkErrorException {

        usernameUpdateHttpEndpointServerError();
        UpdateUsernameUseCaseSync.UseCaseResult useCaseResult
                = SUT.updateUsernameSync(USER_ID, USERNAME);
        verifyNoMoreInteractions(eventBusPoster);

    }

    @Test
    public void updateUser_networkError_eventPostNoInteraction() throws NetworkErrorException {

        usernameUpdateHttpEndpointNetworkError();
        UpdateUsernameUseCaseSync.UseCaseResult useCaseResult
                = SUT.updateUsernameSync(USER_ID, USERNAME);
        verifyNoMoreInteractions(eventBusPoster);

    }

    private void usernameUpdateHttpEndpointSuccess() throws NetworkErrorException {
        Mockito.when(usernameHttpEndpointSync.
                updateUsername(ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
                .thenReturn(new UpdateUsernameHttpEndpointSync.EndpointResult(
                        UpdateUsernameHttpEndpointSync.EndpointResultStatus.SUCCESS, USER_ID, USERNAME));
    }

    private void usernameUpdateHttpEndpointServerError() throws NetworkErrorException {
        Mockito.when(usernameHttpEndpointSync.
                updateUsername(ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
        .thenReturn(new UpdateUsernameHttpEndpointSync.EndpointResult(
                UpdateUsernameHttpEndpointSync.EndpointResultStatus.SERVER_ERROR, USER_ID, USERNAME));
    }

    private void usernameUpdateHttpEndpointAuthError() throws NetworkErrorException {
        Mockito.when(usernameHttpEndpointSync.
                updateUsername(ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
                .thenReturn(new UpdateUsernameHttpEndpointSync.EndpointResult(
                        UpdateUsernameHttpEndpointSync.EndpointResultStatus.AUTH_ERROR, USER_ID, USERNAME));
    }

    private void usernameUpdateHttpEndpointGeneralError() throws NetworkErrorException {
        Mockito.when(usernameHttpEndpointSync.
                updateUsername(ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
                .thenReturn(new UpdateUsernameHttpEndpointSync.EndpointResult(
                        UpdateUsernameHttpEndpointSync.EndpointResultStatus.GENERAL_ERROR,
                        null, null));
    }

    private void usernameUpdateHttpEndpointNetworkError() throws NetworkErrorException {
        Mockito.when(usernameHttpEndpointSync.
                updateUsername(ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
                .thenThrow(new NetworkErrorException());
    }

}