package com.main.service;

import com.main.repository.db.SocialEventDbRepository;

public class SocialEventService {
    final SocialEventDbRepository repo;

    public SocialEventService (SocialEventDbRepository repo) {
        this.repo = repo;
    }
}
