function delayedFailure(value, delay) {
	return new Promise((resolve, reject) => {
		// after 'delay' milliseconds, reject the promise with 'value'
		setTimeout(() => { reject(value) }, delay)
	})
}

const promise = delayedFailure(1234, 3000)

console.log(":: promise created ::")

/*
// Process exceptions using 'catch'
promise.catch(value => {
	// will run only after the promise is rejected
	console.log(`-- failure data: ${ value } --`)
})
*/

// Process exceptions using 'then' with two callbacks,
// one for success and another for failure
promise.then(
	value => {
		// will run only if the promise becomes fulfilled
		console.log(`++ value produced: ${ value } ++`)
	},
	exception => {
		// will run only if the promise becomes rejected
		console.log(`-- failure data: ${ exception } --`)
	}
)

console.log(":: callback registered ::")

console.log(":: waiting... ::")
