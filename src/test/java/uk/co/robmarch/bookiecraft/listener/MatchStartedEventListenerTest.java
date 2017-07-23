package uk.co.robmarch.bookiecraft.listener;

import com.google.common.collect.Sets;
import org.bukkit.Server;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.co.robmarch.bookiecraft.event.MatchStartedEvent;
import uk.co.robmarch.bookiecraft.model.Match;
import uk.co.robmarch.bookiecraft.registry.MatchRegistry;
import uk.co.robmarch.bookiecraft.util.SingletonWrapper;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class MatchStartedEventListenerTest {

    @Mock
    private SingletonWrapper singletonWrapper;

    @Mock
    private Server mockBukkitServer;

    @Mock
    private MatchRegistry mockMatchRegistry;

    private MatchStartedEventListener underTest;

    @Before
    public void setUp() {
        given(singletonWrapper.getBukkitServer()).willReturn(mockBukkitServer);
        given(singletonWrapper.getMatchRegistry()).willReturn(mockMatchRegistry);
        underTest = new MatchStartedEventListener().setSingletonWrapper(singletonWrapper);
    }

    @Test
    public void shouldNotStartTheMatchIfNoEventIsReceived() {
        // When
        underTest.onMatchStarted(null);

        // Then
        verify(mockMatchRegistry, never()).register(any(Match.class));
    }

    @Test
    public void shouldNotStartTheMatchIfTheNameOfTheMatchToBeStartedIsNotProvided() {
        // When
        underTest.onMatchStarted(new MatchStartedEvent(null, "a", "b"));

        // Then
        verify(mockMatchRegistry, never()).register(any(Match.class));
    }

    @Test
    public void shouldNotStartTheMatchIfTheNamesOfTheCompetitorsAreNotProvided() {
        // When
        underTest.onMatchStarted(new MatchStartedEvent("A Match", (Set<String>) null));

        // Then
        verify(mockMatchRegistry, never()).register(any(Match.class));
    }

    @Test
    public void shouldNotStartTheMatchIfThereAreNoCompetitors() {
        // When
        underTest.onMatchStarted(new MatchStartedEvent("A Match", new HashSet<>()));

        // Then
        verify(mockMatchRegistry, never()).register(any(Match.class));
    }

    @Test
    public void shouldNotStartTheMatchIfTheMatchIsAlreadyInProgress() {
        // Given
        given(mockMatchRegistry.get("A Match")).willReturn(new Match("A Match", Sets.newHashSet("a", "b")));

        // When
        underTest.onMatchStarted(new MatchStartedEvent("A Match", "a", "b"));

        // Then
        verify(mockMatchRegistry, never()).register(any(Match.class));
    }

    @Test
    public void shouldStartTheMatchIfTheEventHasAllTheCorrectData() {
        // Given
        ArgumentCaptor<Match> registeredMatchCaptor = ArgumentCaptor.forClass(Match.class);
        given(mockMatchRegistry.get("A Match")).willReturn(null);

        // When
        underTest.onMatchStarted(new MatchStartedEvent("A Match", "a", "b"));

        // Then
        verify(mockMatchRegistry).register(registeredMatchCaptor.capture());
        assertThat(registeredMatchCaptor.getValue().getName(), is("A Match"));
        assertThat(registeredMatchCaptor.getValue().getCompetitors(), contains("a", "b"));
        assertThat(registeredMatchCaptor.getValue().isBettingOpen(), is(true));
        assertThat(registeredMatchCaptor.getValue().getBetRegistry().getAllBets(), is(empty()));
    }

}