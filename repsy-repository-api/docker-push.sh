#!/bin/bash

# Repsy Docker Registry'ye giriş yapma
docker login repsy.io -u ahmetkrtl -p 

# Docker imajını etiketleme
docker tag repsy-repository-api:1.0.0 repsy.io/ahmetkrtl/repsy-repository-api:1.0.0

# Docker imajını Repsy'ye gönderme
docker push repsy.io/ahmetkrtl/repsy-repository-api:1.0.0 