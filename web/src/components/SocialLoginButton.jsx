import googleLogo from '../assets/google.png'
import appleLogo from '../assets/apple.png'

function SocialLoginButton({ provider, onClick }) {
  const isGoogle = provider === 'google'
  const isApple = provider === 'apple'

  const getIcon = () => {
    switch (provider) {
      case 'google':
        return <img src={googleLogo} alt="Google logo" className="w-6 h-6" />
      case 'apple':
        return <img src={appleLogo} alt="Apple logo" className="w-8 h-8" />
      default:
        return '🔑'
    }
  }

  const getLabel = () => {
    switch (provider) {
      case 'google':
        return 'Continue with Google'
      case 'apple':
        return 'Continue with Apple'
      default:
        return 'Continue'
    }
  }

  return (
    <button
      onClick={onClick}
      className={`w-full max-w-sm h-10 px-4 rounded-xl text-sm font-medium flex items-center justify-center gap-2 mb-2
        ${isGoogle ? 'bg-white text-gray-800 border border-gray-300' : ''}
        ${isApple ? 'bg-black text-white' : ''}
      `}
    >
      <span>{getIcon()}</span>
      <span>{getLabel()}</span>
    </button>
  )
}

export default SocialLoginButton
