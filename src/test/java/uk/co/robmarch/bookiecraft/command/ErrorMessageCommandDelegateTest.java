package uk.co.robmarch.bookiecraft.command;

import org.bukkit.command.CommandSender;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ErrorMessageCommandDelegateTest {

    @Test
    public void shouldSendMessageToUser() {
        // Given
        CommandSender commandSender = mock(CommandSender.class);
        ErrorMessageCommandDelegate underTest = new ErrorMessageCommandDelegate("Oh Lordy! Something done fucked up mightily!");

        // When
        boolean result = underTest.onCommand(commandSender, null, null, null);

        // Then
        verify(commandSender).sendMessage("Oh Lordy! Something done fucked up mightily!");
        assertThat(result, is(false));
    }
}