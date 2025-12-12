import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axiosInstance from '../api/axiosInstance';
import { saveToken } from '../utils/tokenStorage';

// TODO: 국가코드에 따라 전화번호 포맷을 다르게 처리하는 로직 추가
// TODO: 회원가입 성공시 /projects로 리다이렉트
// TODO: 회원가입 실패시 에러 메시지 표시
//       서버와 통신 성공했다면 error.code 로 에러코드 오고, 아니면 error.message 로 에러 메시지 옴

function RegisterUserPage() {
  const [form, setForm] = useState({
    name: '',
    email: '',
    gender: '',
    phoneNumber: '',
    nationalCode: 'KR', // 디폴트값 설정
    dateOfBirth: '',
  });
  const [submitted, setSubmitted] = useState(false);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    const urlParams = new URLSearchParams(window.location.search);
    const accessToken = urlParams.get('accessToken');
    if (accessToken) {
      saveToken(accessToken);
      window.history.replaceState(null, '', '/projects');
    }
  }, []);

  const handleChange = (e) => {
    const { name, value } = e.target;
    if (name === 'phoneNumber') {
      // 국가코드에 따라 포맷 분기 (KR, US, JP)
      let digits = value.replace(/\D/g, '');
      let formatted = '';
      if (form.nationalCode === 'KR') {
        // 한국: 010-1234-5678
        if (digits.length <= 3) formatted = digits;
        else if (digits.length <= 7) formatted = digits.slice(0, 3) + '-' + digits.slice(3);
        else if (digits.length <= 11) formatted = digits.slice(0, 3) + '-' + digits.slice(3, 7) + '-' + digits.slice(7, 11);
        else formatted = digits.slice(0, 3) + '-' + digits.slice(3, 7) + '-' + digits.slice(7, 11);
      } else if (form.nationalCode === 'US') {
        // 미국: 123-456-7890
        if (digits.length <= 3) formatted = digits;
        else if (digits.length <= 6) formatted = digits.slice(0, 3) + '-' + digits.slice(3);
        else formatted = digits.slice(0, 3) + '-' + digits.slice(3, 6) + '-' + digits.slice(6, 10);
      } else if (form.nationalCode === 'JP') {
        // 일본: 090-1234-5678
        if (digits.length <= 3) formatted = digits;
        else if (digits.length <= 7) formatted = digits.slice(0, 3) + '-' + digits.slice(3);
        else formatted = digits.slice(0, 3) + '-' + digits.slice(3, 7) + '-' + digits.slice(7, 11);
      } else {
        formatted = digits;
      }
      setForm((prev) => ({ ...prev, [name]: formatted }));
    } else if (name === 'nationalCode') {
      let code = value;
      if (value === '+82') code = 'KR';
      else if (value === '+1') code = 'US';
      else if (value === '+81') code = 'JP';
      setForm((prev) => ({ ...prev, nationalCode: code, phoneNumber: '' }));
    } else {
      setForm((prev) => ({ ...prev, [name]: value }));
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSubmitted(false);
    try {
      const submitForm = { ...form, phoneNumber: form.phoneNumber.replace(/-/g, '') };
      await axiosInstance.patch('/auth/register/pm', submitForm);
      setSubmitted(true);
      alert('회원가입이 완료되었습니다.');
      navigate('/projects');
    } catch (err) {
      if (err.response?.data?.error?.code) {
        alert(`에러 코드: ${err.response.data.error.code}`);
      } else {
        alert(`회원가입 실패: ${err.message}`);
      }
      setError('Registration failed.');
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-neutral-200 p-6">
      <form onSubmit={handleSubmit} className="bg-white rounded-2xl shadow-2xl p-10 w-full max-w-lg border border-indigo-100 space-y-6">
        <h2 className="text-3xl font-bold text-center text-indigo-700 mb-6">Sign Up</h2>
        <div className="flex flex-col gap-2">
          <label className="font-semibold text-indigo-700">Name</label>
          <input name="name" value={form.name} onChange={handleChange} required className="border-2 border-indigo-200 rounded-lg px-4 py-2 focus:ring-2 focus:ring-indigo-400 transition" placeholder="Name" />
        </div>
        <div className="flex flex-col gap-2">
          <label className="font-semibold text-indigo-700">Email</label>
          <input name="email" type="email" value={form.email} onChange={handleChange} required className="border-2 border-indigo-200 rounded-lg px-4 py-2 focus:ring-2 focus:ring-indigo-400 transition" placeholder="Email" />
        </div>
        <div className="flex flex-col gap-2">
          <label className="font-semibold text-indigo-700">Gender</label>
          <select name="gender" value={form.gender} onChange={handleChange} required className="border-2 border-indigo-200 rounded-lg px-4 py-2 focus:ring-2 focus:ring-indigo-400 transition">
            <option value="">Select</option>
            <option value="MALE">Male</option>
            <option value="FEMALE">Female</option>
            <option value="OTHER">Other</option>
          </select>
        </div>
        <div className="flex flex-col gap-2">
          <label className="font-semibold text-indigo-700">Phone Number</label>
          <div className="flex gap-2">
            <select name="nationalCode" value={form.nationalCode === 'KR' ? '+82' : form.nationalCode === 'US' ? '+1' : form.nationalCode === 'JP' ? '+81' : form.nationalCode} onChange={handleChange} className="border-2 border-indigo-200 rounded-lg px-2 py-2 focus:ring-2 focus:ring-indigo-400 transition">
              <option value="+82">+82</option>
              <option value="+1">+1</option>
              {/* <option value="+81">+81</option> */}
              {/* Add more country codes if needed */}
            </select>
            <input name="phoneNumber" value={form.phoneNumber} onChange={handleChange} required className="flex-1 border-2 border-indigo-200 rounded-lg px-4 py-2 focus:ring-2 focus:ring-indigo-400 transition" placeholder="Phone Number" inputMode="numeric" pattern="[0-9-]*" maxLength={15} />
          </div>
        </div>
        <div className="flex flex-col gap-2">
          <label className="font-semibold text-indigo-700">Date of Birth</label>
          <input name="dateOfBirth" type="date" value={form.dateOfBirth} onChange={handleChange} required className="border-2 border-indigo-200 rounded-lg px-4 py-2 focus:ring-2 focus:ring-indigo-400 transition" />
        </div>
        <button type="submit" className="w-full bg-gradient-to-r from-indigo-500 to-blue-500 hover:from-indigo-600 hover:to-blue-600 text-white font-bold px-8 py-3 rounded-xl shadow-lg transition text-lg mt-4">Sign Up</button>
        {submitted && <div className="text-green-600 text-center font-semibold mt-4">Registration completed!</div>}
        {error && <div className="text-red-600 text-center font-semibold mt-4">Registration failed.</div>}
      </form>
    </div>
  );
}

export default RegisterUserPage;
