package ru.tbank.springapp.patterns.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.tbank.springapp.client.KudagoClient;
import ru.tbank.springapp.dao.Repository;
import ru.tbank.springapp.model.Category;

@RequiredArgsConstructor
@Slf4j
public class FetchCategoriesCommand implements Command {

    private final KudagoClient client;
    private final Repository<String, Category> repository;

    @Override
    public void execute() {
        log.info("Fetching categories from kudago");
        client.getCategories().forEach(categoryDTO -> {
            repository.save(categoryDTO.slug(), categoryDTO.toCategory());
        });
    }
}
