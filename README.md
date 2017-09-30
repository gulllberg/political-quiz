# Political Quiz

Be able to answer a political quiz to see how well your opinions matches the different political parties in Sweden.

Similar to [this](www.partisk.nu).

## Requirements

The requirements of the application.

#### First-level requirements

1. Each question should be a statement to which you can agree on a scale from 1-6 and rank importance on a scale from 1-6.
2. The question should be answered by political parties as a reference, and by users who takes the test.
3. Depending on answers you be given a score (0-100 %) for each party.

#### Second-level requirements

Three different models will be tested. The user should be able to see the result of all models which make it into the final version.

__Notation__

A<sub>u</sub> = answer for user to a given question  
A<sub>p</sub> = answer for a party to a given question

W<sub>u</sub> = weight for a question given by user  
W<sub>p</sub> = weight for a question given by a party

RW<sub>u</sub> = relative weight for a question for user  
RW<sub>p</sub> = relative weight for a question for a party  
_Relative weight means W/W<sub>tot</sub>_

E = error on a question  
E<sub>tot</sub> = total error

__Model 1__

E = abs(A<sub>u</sub> - A<sub>p</sub>) * (RW<sub>u</sub> + RW<sub>p</sub>)

Max E<sub>tot</sub> = 10  
Min E<sub>tot</sub> = 0  
Quiz result = 100 - 10 * E<sub>tot</sub>

Problem: Same answers but different weights means you might not get the highest score for the party where you agree most on the questions you consider most important.

__Model 2__

E = abs(A<sub>u</sub> - A<sub>p</sub>) * abs(W<sub>u</sub> - W<sub>p</sub>)

Max E<sub>tot</sub> = 25 * number of questions  
Min E<sub>tot</sub> = 0  
Quiz result = 100 - 4 * E<sub>tot</sub> / number of questions

Problem: Questions both consider less important affect the result as much as questions both consider important.  
Problem 2: Different responses for weights affect the result as much as different answers. This is probably not what a user expects.

__Model 3__

E = abs(A<sub>u</sub> - A<sub>p</sub>) * abs(W<sub>u</sub> - W<sub>p</sub>) * (RW<sub>u</sub> + RW<sub>p</sub>) 

Max E<sub>tot</sub> = 25 * 2  
Min E<sub>tot</sub> = 0  
Quiz result = 100 - 2 * E<sub>tot</sub>

Problem: Different responses for weights affect the result as much as different answers. This is probably not what a user expects.

#### Third-level (non-functional) requirements

1. Should be fast and give instant feedback upon completing quiz
2. Should look decent
3. Should work in most browsers

## License

Copyright © 2017 Daniel Gullberg

Distributed under the Eclipse Public License version 1.0
