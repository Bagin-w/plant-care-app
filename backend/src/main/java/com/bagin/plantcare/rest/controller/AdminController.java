package com.bagin.plantcare.rest.controller;

import com.bagin.plantcare.persistence.adapter.PerenualImportAdapter;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

  private final PerenualImportAdapter perenualImportAdapter;

  public AdminController(PerenualImportAdapter perenualImportAdapter) {
    this.perenualImportAdapter = perenualImportAdapter;
  }

  @PostMapping("/import-plants/{page}")
  public String importPlants(@PathVariable int page) {
    int imported = perenualImportAdapter.importPage(page);
    return "Importiert: " + imported + " neue Pflanzenarten von Seite " + page;
  }
}