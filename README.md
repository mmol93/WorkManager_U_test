# WorkManager_U_test

* 강의 보고 배운 것을 복습하기 위해 만들었음
* 참조한 부분은 https://github.com/mmol93/ViewModel_Udemy 의 WorkManager Branch임
* 이 project는 BoilerPlate로 써도좋을거 같음(대부분 사용 방법이 일정하고 키워드만 바꾸면 쓸 수 있음)

* #### WorkManager는 앱이 종료되었거나 기기가 다시 시작하더라도 다시 진행될 필요가 있는 백그라운드 작업에서 사용된다

## WorkManager의 특징
- 제약조건(Constraint)를 이용하여 조건을 걸 수 있다. ex: 인터넷이 연결되었을 때 or 배터리가 충전중일 때
- 한 번 또는 반복적으로 작업 실행
- 진행했던 작업의 자동 저장: 진행했던 모든 작업은 자동으로 내부의 SQLite에 저장되었다가 앱이 실행되면 다시 진행한다
- 작업 순서 제어가능: begin과 then을 이용하여 순서를 지정할 수 있고 병렬 작업도 가능하다

## 테크닉
- 작업을 한번만 실행: OneTimeWorkRequest
- 작업을 반복으로 실행: PeriodicWorkRequest
- 작업을 순서대로 실행: begin(~).then(~)
- 작업을 병렬로 실행: mutableListOf()
- 제약조건 설정(Constraints)
- 실행 결과에 따른 데이터 주고 받기: inputData
