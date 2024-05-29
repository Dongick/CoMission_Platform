import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
  stages: [
    {duration: '1m', target: 50},
    {duration: '2m', target: 50},
    {duration: '1m', target: 200},
    {duration: '2m', target: 200},
    {duration: '2m', target: 0},
  ],
  thresholds: { // 부하 테스트가 언제 성공했다고 할 수 있는지
      http_req_duration: ['p(95)<200'], // 전체 요청의 95%가 100ms 안에 들어오면 성공
  },
};

const BASE_URL = 'http://localhost:8080/api';
const ACCESS_TOKEN = 'access_token'
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

export default function () {
    getPath();
//    postPath();
}