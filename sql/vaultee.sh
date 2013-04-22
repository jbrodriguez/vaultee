#!/bin/bash

cat vaultee_drop.sql vaultee_create.sql vaultee_load.sql > vaultee.sql
psql -U apertoire -d vaultee -f vaultee.sql