package uk.co.robmarch.bookiecraft.command;

import org.bukkit.command.CommandSender;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.co.robmarch.bookiecraft.util.PlayerMessageProvider;
import uk.co.robmarch.bookiecraft.util.SingletonWrapper;

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class BookiecraftCommandTest {

    @Mock
    private SingletonWrapper mockSingletonWrapper;

    @Mock
    private CommandSender commandSender;

    private BookiecraftCommand underTest;

    @Before
    public void setup() {
        underTest = new BookiecraftCommand().setSingletonWrapper(mockSingletonWrapper);
    }

    @Test
    public void shouldUseTheOpenMatchesCommandIfTheSenderDoesNotHaveAdminPrivileges() {
        // Given
        givenTheUserHasAdminPrivileges(false);

        // Then
        thenCommandDelegatesTo(OpenMatchesCommandDelegate.class, "start", "match", "a", "b");
    }

    @Test
    public void shouldUseTheOpenMatchesCommandIfTheSenderDoesNotUseAnyCommandArguments() {
        // Given
        givenTheUserHasAdminPrivileges(true);

        // Then
        thenCommandDelegatesTo(OpenMatchesCommandDelegate.class);
    }

    @Test
    public void shouldUseTheStartMatchCommandIfTheFirstArgumentIsTheStartMatchSubCommand() {
        // Given
        givenTheUserHasAdminPrivileges(true);

        // Then
        thenCommandDelegatesTo(StartMatchCommandDelegate.class, "start");
    }

    @Test
    public void shouldUseTheCloseBettingMatchCommandIfTheFirstArgumentIsTheCloseBettingSubCommand() {
        // Given
        givenTheUserHasAdminPrivileges(true);

        // Then
        thenCommandDelegatesTo(CloseBettingCommandDelegate.class, "close");
    }

    @Test
    public void shouldUseTheEndMatchCommandIfTheFirstArgumentIsTheEndMatchSubCommand() {
        // Given
        givenTheUserHasAdminPrivileges(true);

        // Then
        thenCommandDelegatesTo(EndMatchCommandDelegate.class, "end");
    }

    @Test
    public void shouldShowAGenericErrorIfTheFirstArgumentIsNonsense() {
        // Given
        givenTheUserHasAdminPrivileges(true);

        // Then
        thenCommandDelegatesTo(ErrorMessageCommandDelegate.class, "not a valid sub-command");
    }

    @Test
    public void shouldExecuteTheRelevantCommandDelegate() {
        // Given
        givenTheUserHasAdminPrivileges(true);

        // When
        whenInvalidSubCommandIsUsed();

        // Then
        thenErrorMessageCommandDelegateExecutes();
    }

    private void givenTheUserHasAdminPrivileges(boolean hasAdminPrivileges) {
        given(commandSender.hasPermission("bookiecraft.admin")).willReturn(hasAdminPrivileges);
    }

    private void whenInvalidSubCommandIsUsed() {
        underTest.onCommand(commandSender, null, null, new String[]{ "Not a valid sub-command" });
    }

    private <T extends BookiecraftCommandDelegate> void thenCommandDelegatesTo(Class<T> delegateType, String... commandArgs) {
        assertThat(underTest.getCommandDelegate(commandSender, commandArgs), instanceOf(delegateType));
    }

    private void thenErrorMessageCommandDelegateExecutes() {
        verify(commandSender).sendMessage(PlayerMessageProvider.getGenericError());
    }

}