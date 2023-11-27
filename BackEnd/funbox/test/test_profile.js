const accessToken =
  'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6NzgsInVzZXJuYW1lIjpudWxsLCJpYXQiOjE3MDEwODAyOTAsImV4cCI6MTcwMTMzOTQ5MH0.W4iJQtlHwjqR5YOgOlLyQxCd-JtKsFrEVh8ptUOP1Fs';

// fetch('http://localhost:3000/users/profile', {
//   method: 'POST',
//   headers: {
//     'Content-Type': 'application/json',
//   },
//   body: JSON.stringify({
//     accessToken: accessToken,
//     profileUrl: 'hello',
//   }),
// })
//   .then((res) => {
//     return res.json();
//   })
//   .then((data) => {
//     console.log('------------------');
//     console.log('profile_url 변경 확인');
//     console.log(data);
//   });

const fs = require('fs');
const FormData = require('form-data');
const fetch = require('node-fetch');

const filePath = `./test.png`;
const form = new FormData();
const stats = fs.statSync(filePath);
const fileSizeInBytes = stats.size;
const fileStream = fs.createReadStream(filePath);
form.append('file', fileStream, { knownLength: fileSizeInBytes });
form.append('accessToken', accessToken);

fetch('http://localhost:3000/users/uploadtest', {
  method: 'POST',
  headers: {
    Authorization: `Bearer ${accessToken}`,
  },
  credentials: 'include',
  body: form,
})
  .then((res) => {
    return res.json();
  })
  .then((data) => {
    console.log(data);
  });
