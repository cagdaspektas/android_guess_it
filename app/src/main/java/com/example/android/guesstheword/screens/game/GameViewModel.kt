

package com.example.android.guesstheword.screens.game

import android.os.CountDownTimer
import android.text.format.DateUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
private val CORRECT_BUZZ_PATTERN = longArrayOf(100, 100, 100, 100, 100, 100)
private val PANIC_BUZZ_PATTERN = longArrayOf(0, 200)
private val GAME_OVER_BUZZ_PATTERN = longArrayOf(0, 2000)
private val NO_BUZZ_PATTERN = longArrayOf(0)
class GameViewModel: ViewModel() {

    companion object{
        //bu kısım oyun bittiğinde
        const val DONE= 0L
        //buzz kısmı için
        private const val COUNTDOOWN_PANIC_SECONDS=10L
        //saniyedeki milisaniye kısmı
        const val ONE_SECOND= 1000L
        //oyunun toplam zamanı
        const val  COUNTDOWN_TIME= 60000L
    }
    enum class BuzzType(val pattern: LongArray) {
        CORRECT(CORRECT_BUZZ_PATTERN),
        GAME_OVER(GAME_OVER_BUZZ_PATTERN),
        COUNTDOWN_PANIC(PANIC_BUZZ_PATTERN),
        NO_BUZZ(NO_BUZZ_PATTERN)
    }
    private val timer:CountDownTimer

    // The current word
   private  val _word = MutableLiveData<String>()
    val word:LiveData<String>
    get() = _word

    // The current score
  private   val _score = MutableLiveData<Int>()
    val score:LiveData<Int>
    get() = _score


    // The list of words - the front of the list is the next word to guess
    private lateinit var wordList: MutableList<String>

    private val _eventGameFinish=MutableLiveData<Boolean>()
    val eventGameFinish:LiveData<Boolean>
    get() = _eventGameFinish

    private val _buzzEvent= MutableLiveData<BuzzType>()
    val buzzEvent:LiveData<BuzzType>
    get() = _buzzEvent

    private val _currentTime=MutableLiveData<Long>()
    val currentTime:LiveData<Long>
    get() = _currentTime

    val  currentTimeString = Transformations.map(currentTime,
        {
        time->DateUtils.formatElapsedTime(time)
    })


    init {
        Log.i("GameViewModel","GameViewModel Created")
        _eventGameFinish.value=false
        resetList()
        nextWord()
        _score.value=0
        timer = object :CountDownTimer(COUNTDOWN_TIME, ONE_SECOND){
            override fun onTick(millisUntilFinished: Long) {
            _currentTime.value=(millisUntilFinished/ ONE_SECOND)
                if (millisUntilFinished/ ONE_SECOND<= COUNTDOOWN_PANIC_SECONDS){
                    _buzzEvent.value=BuzzType.COUNTDOWN_PANIC
                }
            }

            override fun onFinish() {
              _currentTime.value= DONE
                _buzzEvent.value=BuzzType.GAME_OVER
                _eventGameFinish.value=true
            }
        }
        timer.start()


    }

    override fun onCleared() {
        super.onCleared()
        Log.i("GameViewModel","GameviewModel clear called")
    }

    private fun resetList() {
        wordList = mutableListOf(
            "queen",
            "hospital",
            "basketball",
            "cat",
            "change",
            "snail",
            "soup",
            "calendar",
            "sad",
            "desk",
            "guitar",
            "home",
            "railway",
            "zebra",
            "jelly",
            "car",
            "crow",
            "trade",
            "bag",
            "roll",
            "bubble"
        )
        wordList.shuffle()
    }
    /**
     * Moves to the next word in the list
     */
    private fun nextWord() {
        //Select and remove a word from the list
        if (wordList.isEmpty()) {
            resetList()
        }
            _word.value = wordList.removeAt(0)


    }
     fun onSkip() {
        _score.value= score.value?.minus(1)
        nextWord()
    }

     fun onCorrect() {
        _score.value=score.value?.plus(1)
         _buzzEvent.value=BuzzType.CORRECT
        nextWord()
    }
    fun  onGameFinishComplete(){
        super.onCleared()
        timer.cancel()
    }
    fun onBuzzComplete() {
        _buzzEvent.value = BuzzType.NO_BUZZ
    }
}