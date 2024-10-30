package ru.tbank.springapp.patterns.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import ru.tbank.springapp.aspect.Timed;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class DataFetcher {

    private final List<Command> fetchCommands;

    @Timed
    @EventListener(ApplicationReadyEvent.class)
    void fetchData() {
        fetchCommands.forEach(Command::execute);
    }

}
