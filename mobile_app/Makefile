.PHONY: all run_app_with_tests run_app analyze format run_unit_tests

all: analyze format run_app_with_tests

run_app_with_tests: run_unit_tests run_app

run_app:
	flutter run

analyze:
	flutter analyze --no-fatal-infos

format:
	flutter format --set-exit-if-changed .

run_unit_tests:
	flutter test