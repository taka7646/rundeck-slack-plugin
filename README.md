# rundeck-slack-plugin Rundeck Plugin


### プロジェクト作成
```
./rundeck-plugin-bootstrap-0.4.11/bin/rundeck-plugin-bootstrap -d . -n rundeck-slack-plugin -s Notification -t java
```
https://github.com/rundeck/plugin-bootstrap

## ビルド
```
gradle build
```

## JOB変数の置換
* {ID} JobID
* {JOB} Jobの名前
* {STATUS} succeeded / failed 
* {URL} JobのURL
* {USER} Jobを実行したユーザー
* {NODE} Jobを実行したNode
* {OPT:XXXX} Jobオプション
