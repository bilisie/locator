#!/bin/sh

exec java $JAVA_OPTS -cp "/locator/lib/*:/locator/*" com.bilisie.locator.service.Locator

exec "$@"