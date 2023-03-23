Чтобы сгенерировать классы для Dart нужно выполнить команду
из папки proto3/src/main
```shell
 protoc --dart_out=../../generated_proto/dart -I./proto ./proto/energy_drinks_grpc_api.proto 
```

Перед этим потавить плагин и возможно добавить в zshrc или bashrc
```shell
dart pub global activate protoc_plugin
export PATH=$PATH:~/.pub-cache/bin
```
