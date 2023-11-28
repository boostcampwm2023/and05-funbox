const accessToken =
  'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6NzgsInVzZXJuYW1lIjpudWxsLCJpYXQiOjE3MDEwODAyOTAsImV4cCI6MTcwMTMzOTQ5MH0.W4iJQtlHwjqR5YOgOlLyQxCd-JtKsFrEVh8ptUOP1Fs';

fetch('http://localhost:3000/auth/notoken/test', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
  },
  body: JSON.stringify({ naverFakeId: 'test1' }),
})
  .then((res) => {
    return res.json();
  })
  .then((data) => {
    console.log('------------------');
    console.log('테스트 : 네이버 Fake id로 user DB 생성');
    console.log(data);
  });

fetch('http://localhost:3000/users/username', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
    Authorization: `Bearer ${accessToken}`,
  },
  body: JSON.stringify({
    userName: 'hi',
  }),
})
  .then((res) => {
    return res.json();
  })
  .then((data) => {
    console.log('------------------');
    console.log('username 변경 확인');
    console.log(data);
  });
