# Food Recommendation Application
# 음식 추천해주는 어플리케이션 (작성중)
![image](https://img.shields.io/badge/language-JAVA-blue?style=flat-square&logo=Android-Studio)
![image](https://img.shields.io/badge/Latest%20Update-200817-9cf?style=flat-square)
![HitCount](http://hits.dwyl.com/minji-o-j/Food-Recommendation-Application.svg)  
---
<br>

[요약](#-요약)
[개발 기간](#-개발-)
[개발자](#-개발자)
<!--
[](#-)
-->


---

## ◼ 요약
- 심장 박동을 이용하여 사용자의 기분을 판단. 사용자의 기분을 개선해주는 메뉴를 추천하는 어플리케이션 개발 

- 감정은 __행복/슬픔/화남__ 으로 분류

- 어플에는 일반 메뉴 선택 기능, 인기 차트에서 메뉴를 선택하는 기능, 감정에 따른 음식을 추천하는 기능이 있음

- __PPG 센서__ 를 이용하여 사용자의 무의식에서 나온 감정을 측정한 후 __사용자의 기분을 개선하는 효과가 있는 음식__ 을 추천함.
---
## ◼ 개발 기간
- 2019/10/30 ~ 2019/12/16

---
## ◼ 개발자
- 박현지, 이은후([@201810794](https://github.com/201810794)), 정민지([@minji-o-j](https://github.com/minji-o-j/))
---
## ◼ 사용 장비 & 프로그램
- PPG 센서
- Android Studio
---
## ◼ 시스템 설명
![image](https://user-images.githubusercontent.com/45448731/78692592-19205b80-7935-11ea-952c-5e5118284415.png)  

> PPG센서를 이용하여 사용자의 감정을 측정한 후 화면에 나타낸다.  
<br>
<br>

![image](https://user-images.githubusercontent.com/45448731/78692749-4a992700-7935-11ea-881f-a91fa9537252.png)  
>  __행복/ 화남/ 슬픔__ 세 감정이 있으며 감정 상태에 따라 추천하는 음식 List 중 __3개가 랜덤으로 선택__ 된다.  
<br>
<br>

![image](https://user-images.githubusercontent.com/45448731/78698294-13c70f00-793d-11ea-94ab-fee017cf5882.png)  
> 선택된 메뉴에 대해서는 각각 __이 음식이 추천된 이유에 대한 설명__ 을 확인 할 수 있으며 결제를 하여 음식을 주문할 수 있다.  
<br>

---
## ◼ 목적 및 기대 효과
- 배달 어플에 다양한 종류의 음식이 있지만 어떤 음식을 먹을지 결정하기 힘들거나, 음식을 먹음으로써 기분이 현재보다 더 나아지기를 원하는 사람들을 위한 시스템

- 실제로 사용자가 먹은 음식이 사용자의 기분에 큰 영향을 미치므로, 추천된 음식을 주문해 먹음으로써 기분이 개선되는 효과를 누릴 것으로 예상

- 사용자의 현재 감정을 개선하는 음식을 제공 해준다는 점에서 사용자의 흥미를 끌 것으로 예상
---
## ◼ 감성 측정 방법

### > 측정하는 Data
#### PPI
- Peak와 Peak 사이 간격인 PPI(Peak-to-Peak Interval)를 측정하여 감성 판단   
![image](https://user-images.githubusercontent.com/45448731/78755071-88d52b80-79b3-11ea-828c-e3b6ec9d824a.png)
> __PPI 감소__: BPM 증가, 교감 신경 활성화
> __PPI 증가__: BPM 감소, 부교감 신경 활성화
<br>

#### HRV
![image](https://user-images.githubusercontent.com/45448731/78755943-f2096e80-79b4-11ea-8d3b-cb185937b69a.png)
> HF(고주파 성분): 부교감 신경계의 활동과 관련 
> LF(저주파 성분): 교감 신경계의 활동과 관련  
> HF/LF: 교감신경계와 부교감 신경계의 우세도 판별  
<br>

### > 감정 분류 
![image](https://user-images.githubusercontent.com/45448731/78754937-49a6da80-79b3-11ea-883d-92aaaf39c3df.png)

### > 측정 방법
- PPG 센서를 어플리케이션을 통해 스마트폰에 연결한다. 

---

## 

---
## ◼ 설명
  - 
---
