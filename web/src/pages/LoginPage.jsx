import SocialLoginButton from '../components/SocialLoginButton'
import { loginWithProvider } from '../services/authService'
import monoramaLogo from '../assets/monorama.png'

function LoginPage() {
  return (
    <div className="min-h-screen flex justify-center items-center bg-neutral-200 px-4">
      <div className="bg-white rounded-2xl shadow-md px-8 py-10 flex flex-col md:flex-row items-center gap-10 max-w-4xl w-full">
        {/* 왼쪽 로고 */}
        <div className="flex flex-col items-center">
          <img
            src={monoramaLogo}
            alt="Monorama"
            className="w-64 h-64 object-contain"
          />
          <span className="text-sm text-gray-500 mt-2">by Monorama</span>
        </div>

        {/* 오른쪽 로그인 영역 */}
        <div className="flex flex-col items-center">
          <p className="text-xl text-center font-medium mb-6">
            A website for creating projects<br />
            to collect health data & air quality data
          </p>
          <SocialLoginButton provider="google" onClick={() => loginWithProvider('google')} />
          <SocialLoginButton provider="apple" onClick={() => loginWithProvider('apple')} />
        </div>
      </div>
    </div>
  )
}

export default LoginPage
