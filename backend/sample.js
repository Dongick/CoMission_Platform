import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
  stages: [
    {duration: '2m', target: 50},
    {duration: '1m', target: 200},
    {duration: '2m', target: 200},
    {duration: '2m', target: 0},
  ],
  thresholds: { // 부하 테스트가 언제 성공했다고 할 수 있는지
      http_req_duration: ['p(95)<200'], // 전체 요청의 95%가 100ms 안에 들어오면 성공
  },
};

const BASE_URL = 'http://api.comission-platform.store/api';
const LOCAL_BASE_URL = 'http://localhost:8080/api';
const ACCESS_TOKEN = 'eyJhbGciOiJIUzI1NiJ9.eyJjYXRlZ29yeSI6ImFjY2VzcyIsInVzZXJuYW1lIjoi7J2064-Z7J21Iiwicm9sZSI6IlJPTEVfVVNFUiIsImVtYWlsIjoiZGxlaGRkbHIzMjE5QG5hdmVyLmNvbSIsImlhdCI6MTcxODM0MTQyNCwiZXhwIjoxNzE4MzQzMjI0fQ.mZwCkhiq4QuREh1eHKdhj4c_LFKx090JEcmFXcn8xZ8'
//const photoData = open('src/main/resources/static/missions/424c1851-51c8-438a-b607-755412d86bcc.jpg', 'b');

function getPath(){

  let res = http.get(`${BASE_URL}/main`, {
    tags: {
        endpoint: 'get_path',
    },
  });

  // 응답 체크
  check(res, {
    'status was 200': (r) => r.status == 200,
  });

  // 1초 대기
  sleep(1);
}

function postPath(){
    const params = {
        headers: {
            'Authorization': `Bearer ${ACCESS_TOKEN}`,
//            'Content-Type': 'application/json',
        },
    };

    const missionInfo = JSON.stringify({
        title: 'Test Mission',
        description: 'This is a test mission description',
        minParticipants: 2,
        duration: 30,
        frequency: '매일',
    });

    const formData = {
        'missionInfo': http.file(missionInfo, 'missionInfo.json', 'application/json'),
//        'photoData': http.file(photoData, 'photo.png'),
    };


    let res = http.post(`${BASE_URL}/mission`, formData, params, {
        tags: {
            endpoint: 'post_path',
        },
    });

    check(res, {
        'status was 201': (r) => r.status === 201,
    });

    // 1초 대기
    sleep(1);
}

function getPerformanceTestPath(){

  const params = {
          headers: {
              'Authorization': `Bearer ${ACCESS_TOKEN}`,
  //            'Content-Type': 'application/json',
          },
      };

  let res = http.get(`${LOCAL_BASE_URL}/performance/authentication/6662b7507c6e39272a2ec010/0`, params, {
    tags: {
        endpoint: 'get_path',
    },
  });

  // 응답 체크
  check(res, {
    'status was 200': (r) => r.status == 200,
  });

  // 1초 대기
  sleep(1);
}

export default function () {
    getPerformanceTestPath()
}