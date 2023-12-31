const accessToken =
  'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6ODEsImlhdCI6MTcwMTU4NTgyOSwiZXhwIjoxNzAxODQ1MDI5fQ.pXD_hEQ61MKOVzodUXo9j_3h_UCJmZcGDDAQ__2crI4';
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

fetch('http://localhost:3000/users/profile', {
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
    console.log('------------유저 프로필 갱신');
    console.log(data);
  });

// fetch('http://localhost:3000/users/profile', {
//   method: 'DELETE',
//   headers: {
//     Authorization: `Bearer ${accessToken}`,
//   },
// })
//   .then((res) => {
//     return res.json();
//   })
//   .then((data) => {
//     console.log('------------유저 프로필 삭제');
//     console.log(data);
//   });
